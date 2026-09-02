package com.praveen.career.matching;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class MatchingService {

    private static final Map<String, List<String>> SKILLS = new LinkedHashMap<>();
    static {
        SKILLS.put("java", List.of("java", "jdk"));
        SKILLS.put("spring boot", List.of("spring boot", "springboot"));
        SKILLS.put("spring webflux", List.of("spring webflux", "webflux", "reactive programming"));
        SKILLS.put("spring security", List.of("spring security"));
        SKILLS.put("microservices", List.of("microservices", "microservice architecture"));
        SKILLS.put("rest api", List.of("rest api", "restful", "rest services"));
        SKILLS.put("graphql", List.of("graphql"));
        SKILLS.put("python", List.of("python"));
        SKILLS.put("kotlin", List.of("kotlin"));
        SKILLS.put("typescript", List.of("typescript"));
        SKILLS.put("javascript", List.of("javascript"));
        SKILLS.put("angular", List.of("angular"));
        SKILLS.put("react", List.of("react", "reactjs", "react.js"));
        SKILLS.put("kafka", List.of("kafka", "apache kafka"));
        SKILLS.put("rabbitmq", List.of("rabbitmq", "rabbit mq"));
        SKILLS.put("aws", List.of("aws", "amazon web services"));
        SKILLS.put("azure", List.of("azure", "microsoft azure"));
        SKILLS.put("gcp", List.of("gcp", "google cloud"));
        SKILLS.put("docker", List.of("docker", "containerization"));
        SKILLS.put("kubernetes", List.of("kubernetes", "k8s"));
        SKILLS.put("jenkins", List.of("jenkins"));
        SKILLS.put("github actions", List.of("github actions"));
        SKILLS.put("ci/cd", List.of("ci/cd", "continuous integration", "continuous delivery", "continuous deployment"));
        SKILLS.put("postgresql", List.of("postgresql", "postgres"));
        SKILLS.put("mysql", List.of("mysql"));
        SKILLS.put("oracle", List.of("oracle database", "oracle db", "oracle sql"));
        SKILLS.put("sql server", List.of("sql server", "mssql"));
        SKILLS.put("sql", List.of("sql"));
        SKILLS.put("mongodb", List.of("mongodb", "mongo db"));
        SKILLS.put("dynamodb", List.of("dynamodb", "dynamo db"));
        SKILLS.put("cosmos db", List.of("cosmos db", "cosmosdb"));
        SKILLS.put("snowflake", List.of("snowflake"));
        SKILLS.put("redis", List.of("redis"));
        SKILLS.put("linux", List.of("linux", "unix"));
        SKILLS.put("maven", List.of("maven"));
        SKILLS.put("gradle", List.of("gradle"));
        SKILLS.put("junit", List.of("junit"));
        SKILLS.put("mockito", List.of("mockito"));
        SKILLS.put("terraform", List.of("terraform"));
        SKILLS.put("oauth", List.of("oauth", "oauth2", "openid connect"));
    }

    public MatchResponse match(MatchRequest request) {
        String resume = normalize(request.resumeText());
        String job = normalize(request.jobDescription());
        Set<String> required = extract(job);
        Set<String> resumeSkills = extract(resume);
        Set<String> matched = new LinkedHashSet<>(required);
        matched.retainAll(resumeSkills);
        Set<String> missing = new LinkedHashSet<>(required);
        missing.removeAll(resumeSkills);

        int score = required.isEmpty() ? 0 : (int) Math.round(matched.size() * 100.0 / required.size());
        // Avoid presenting a perfect fit when the job text yielded too little technical evidence.
        if (required.size() < 3 && score == 100) score = 70;
        return new MatchResponse(score, matched, missing);
    }

    private Set<String> extract(String text) {
        Set<String> found = new LinkedHashSet<>();
        SKILLS.forEach((canonical, aliases) -> {
            if (aliases.stream().anyMatch(alias -> containsPhrase(text, alias))) found.add(canonical);
        });
        return found;
    }

    private boolean containsPhrase(String text, String phrase) {
        String regex = "(?<![a-z0-9])" + Pattern.quote(phrase) + "(?![a-z0-9])";
        return Pattern.compile(regex).matcher(text).find();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9+#./-]+", " ").replaceAll("\\s+", " ").trim();
    }
}
