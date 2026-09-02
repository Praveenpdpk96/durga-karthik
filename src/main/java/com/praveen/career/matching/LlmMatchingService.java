package com.praveen.career.matching;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LlmMatchingService {
    private final MatchingService matchingService;
    private final ObjectMapper mapper;
    private final RestClient restClient;
    private final boolean enabled;
    private final String apiKey;
    private final String model;

    public LlmMatchingService(
            MatchingService matchingService,
            ObjectMapper mapper,
            RestClient.Builder restClientBuilder,
            @Value("${career.ai.enabled:false}") boolean enabled,
            @Value("${career.ai.api-key:}") String apiKey,
            @Value("${career.ai.model:gpt-5.6-luna}") String model,
            @Value("${career.ai.endpoint:https://api.openai.com/v1/responses}") String endpoint) {
        this.matchingService = matchingService;
        this.mapper = mapper;
        this.enabled = enabled;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
    }

    public LlmAnalysisResponse analyze(MatchRequest request) {
        MatchResponse baseline = matchingService.match(request);
        if (!enabled || apiKey.isBlank()) return fallback(baseline, "AI analysis is not enabled; showing the deterministic baseline.");

        try {
            String prompt = buildPrompt(request, baseline);
            Map<String, Object> body = Map.of("model", model, "input", prompt);
            JsonNode response = restClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            String text = extractOutputText(response);
            if (text == null || text.isBlank()) return fallback(baseline, "The AI provider returned no usable analysis; deterministic results are shown instead.");
            return parse(text, baseline);
        } catch (Exception exception) {
            return fallback(baseline, "AI analysis was unavailable, so the deterministic engine was used safely.");
        }
    }

    private LlmAnalysisResponse parse(String text, MatchResponse baseline) throws Exception {
        String cleaned = text.trim();
        if (cleaned.startsWith("```")) cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        JsonNode json = mapper.readTree(cleaned);
        return new LlmAnalysisResponse(
                "openai-responses-v1", model, true, baseline.score(),
                value(json, "fitAssessment"), value(json, "seniorityAssessment"), value(json, "domainAssessment"),
                list(json, "strengths"), list(json, "gaps"), list(json, "resumeImprovements"), list(json, "interviewFocus"),
                "AI narrative is advisory. The deterministic score remains the auditable baseline."
        );
    }

    private String buildPrompt(MatchRequest request, MatchResponse baseline) {
        return """
                You are a senior technical recruiter and software engineering hiring reviewer.
                Compare the resume with the job description. Do not invent experience. Treat only explicit resume evidence as demonstrated.
                Return ONLY valid JSON with these keys: fitAssessment (string), seniorityAssessment (string), domainAssessment (string), strengths (array of strings), gaps (array of strings), resumeImprovements (array of strings), interviewFocus (array of strings).
                Keep each array to at most 5 concise items. Do not return a numeric score; the application already has an explainable deterministic score.

                Deterministic baseline score: %d
                Recognized matched skills: %s
                Recognized missing skills: %s

                RESUME:
                %s

                JOB DESCRIPTION:
                %s
                """.formatted(baseline.score(), baseline.matchedSkills(), baseline.missingSkills(), limit(request.resumeText()), limit(request.jobDescription()));
    }

    private String limit(String value) {
        if (value == null) return "";
        return value.length() <= 16000 ? value : value.substring(0, 16000);
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) return null;
        JsonNode direct = response.get("output_text");
        if (direct != null && direct.isTextual()) return direct.asText();
        JsonNode output = response.get("output");
        if (output != null && output.isArray()) {
            for (JsonNode item : output) {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    for (JsonNode part : content) {
                        JsonNode text = part.get("text");
                        if (text != null && text.isTextual()) return text.asText();
                    }
                }
            }
        }
        return null;
    }

    private LlmAnalysisResponse fallback(MatchResponse baseline, String note) {
        List<String> strengths = new ArrayList<>(baseline.matchedSkills()).stream().limit(5).toList();
        List<String> gaps = new ArrayList<>(baseline.missingSkills()).stream().limit(5).toList();
        return new LlmAnalysisResponse("deterministic-v3-fallback", model, false, baseline.score(),
                "Use the deterministic weighted score and evidence breakdown as the current fit assessment.",
                "Seniority analysis requires AI enhancement or manual review.",
                "Domain analysis requires AI enhancement or manual review.", strengths, gaps,
                gaps.isEmpty() ? List.of("Quantify architecture, scale, reliability, and business impact in the strongest bullets.") : List.of("Add truthful evidence for the highest-priority gaps where you have relevant experience."),
                List.of("Prepare STAR examples for architecture decisions, production incidents, scale, trade-offs, and ownership."), note);
    }

    private String value(JsonNode node, String key) { JsonNode v=node.get(key); return v==null||v.isNull()?"":v.asText(); }
    private List<String> list(JsonNode node, String key) {
        JsonNode values=node.get(key); if(values==null||!values.isArray()) return List.of();
        List<String> result=new ArrayList<>(); values.forEach(v->{if(v.isTextual()&&result.size()<5)result.add(v.asText());}); return result;
    }
}
