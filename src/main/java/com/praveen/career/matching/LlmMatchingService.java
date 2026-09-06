package com.praveen.career.matching;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;import org.springframework.web.client.RestClient;
import java.util.*;

@Service
public class LlmMatchingService {
    private static final Logger log=LoggerFactory.getLogger(LlmMatchingService.class);
    private final MatchingService matchingService;private final ObjectMapper mapper;private final RestClient restClient;private final boolean enabled;private final String apiKey;private final String model;
    public LlmMatchingService(MatchingService matchingService,ObjectMapper mapper,RestClient.Builder builder,@Value("${career.ai.enabled:false}") boolean enabled,@Value("${career.ai.api-key:}") String apiKey,@Value("${career.ai.model:gpt-5.6-luna}") String model,@Value("${career.ai.endpoint:https://api.openai.com/v1/responses}") String endpoint){this.matchingService=matchingService;this.mapper=mapper;this.enabled=enabled;this.apiKey=apiKey==null?"":apiKey.trim();this.model=model;this.restClient=builder.baseUrl(endpoint).build();}

    public LlmAnalysisResponse analyze(MatchRequest request){
        MatchResponse baseline=matchingService.match(request);
        if(!enabled||apiKey.isBlank())return fallback(baseline,"AI analysis is not enabled; showing the deterministic baseline.");
        try{
            Map<String,Object> body=new LinkedHashMap<>();body.put("model",model);body.put("input",buildPrompt(request,baseline));body.put("max_output_tokens",1800);
            JsonNode response=restClient.post().header(HttpHeaders.AUTHORIZATION,"Bearer "+apiKey).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(JsonNode.class);
            String text=extractOutputText(response);if(text==null||text.isBlank())return fallback(baseline,"The AI provider returned no usable text; deterministic results are shown instead.");return parse(text,baseline);
        }catch(Exception e){
            // Never log the API key, resume, or job description. The exception class/status is enough for deployment diagnosis.
            log.warn("AI review failed using model {}: {}: {}",model,e.getClass().getSimpleName(),safeMessage(e));
            return fallback(baseline,"AI provider request failed ("+e.getClass().getSimpleName()+"). Check Render logs for the provider status; deterministic results remain available.");
        }
    }
    private String safeMessage(Exception e){String m=e.getMessage();if(m==null)return "no message";return m.length()>220?m.substring(0,220):m;}
    private LlmAnalysisResponse parse(String text,MatchResponse baseline)throws Exception{String cleaned=text.trim();if(cleaned.startsWith("```"))cleaned=cleaned.replaceFirst("^```(?:json)?\\s*","").replaceFirst("\\s*```$","");JsonNode j=mapper.readTree(cleaned);return new LlmAnalysisResponse("openai-responses-v1",model,true,baseline.score(),value(j,"fitAssessment"),value(j,"seniorityAssessment"),value(j,"domainAssessment"),list(j,"strengths"),list(j,"gaps"),list(j,"resumeImprovements"),list(j,"interviewFocus"),"AI narrative is advisory. The evidence-weighted deterministic V5 score remains the auditable baseline.");}
    private String buildPrompt(MatchRequest r,MatchResponse b){return """
You are a senior technical recruiter and software engineering hiring reviewer. Compare the resume to the job description. Do not invent experience and do not infer missing credentials. Return ONLY a JSON object with: fitAssessment, seniorityAssessment, domainAssessment, strengths, gaps, resumeImprovements, interviewFocus. The last four are arrays of at most 5 concise strings. Do not return a numeric score. Explicitly call out years, seniority, domain, education/certifications, required responsibilities, and missing requirements when the job states them.
Deterministic baseline: %d. Matched recognized skills: %s. Missing recognized skills: %s.
RESUME:\n%s\nJOB DESCRIPTION:\n%s
""".formatted(b.score(),b.matchedSkills(),b.missingSkills(),limit(r.resumeText()),limit(r.jobDescription()));}
    private String limit(String v){if(v==null)return "";return v.length()<=16000?v:v.substring(0,16000);}
    private String extractOutputText(JsonNode r){if(r==null)return null;JsonNode direct=r.get("output_text");if(direct!=null&&direct.isTextual())return direct.asText();JsonNode output=r.get("output");if(output!=null&&output.isArray())for(JsonNode item:output){JsonNode content=item.get("content");if(content!=null&&content.isArray())for(JsonNode part:content){JsonNode text=part.get("text");if(text!=null&&text.isTextual())return text.asText();}}return null;}
    private LlmAnalysisResponse fallback(MatchResponse b,String note){List<String>s=new ArrayList<>(b.matchedSkills()).stream().limit(5).toList(),g=new ArrayList<>(b.missingSkills()).stream().limit(5).toList();return new LlmAnalysisResponse("deterministic-v5-fallback",model,false,b.score(),"Use the evidence-weighted deterministic score as the current fit assessment.","Seniority analysis requires AI enhancement or manual review.","Domain analysis requires AI enhancement or manual review.",s,g,g.isEmpty()?List.of("Quantify architecture, scale, reliability, and business impact in the strongest bullets."):List.of("Add truthful evidence for the highest-priority gaps where relevant experience exists."),List.of("Prepare STAR examples for architecture decisions, production incidents, scale, trade-offs, and ownership."),note);}
    private String value(JsonNode n,String k){JsonNode v=n.get(k);return v==null||v.isNull()?"":v.asText();}private List<String> list(JsonNode n,String k){JsonNode v=n.get(k);if(v==null||!v.isArray())return List.of();List<String>r=new ArrayList<>();v.forEach(x->{if(x.isTextual()&&r.size()<5)r.add(x.asText());});return r;}
}
