package com.praveen.career.matching;

import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class MatchingService {

    private static final Set<String> SKILLS = Set.of(
            "java", "spring boot", "spring webflux", "angular", "react",
            "python", "kafka", "aws", "azure", "docker", "kubernetes",
            "postgresql", "snowflake", "microservices", "graphql", "redis"
    );

    public MatchResponse match(MatchRequest request) {
        String resume = normalize(request.resumeText());
        String job = normalize(request.jobDescription());

        Set<String> required = new LinkedHashSet<>();
        Set<String> matched = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();

        for (String skill : SKILLS) {
            if (job.contains(skill)) {
                required.add(skill);
                if (resume.contains(skill)) {
                    matched.add(skill);
                } else {
                    missing.add(skill);
                }
            }
        }

        int score = required.isEmpty()
                ? 0
                : (int) Math.round((matched.size() * 100.0) / required.size());

        return new MatchResponse(score, matched, missing);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
