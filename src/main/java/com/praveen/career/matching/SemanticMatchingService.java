package com.praveen.career.matching;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SemanticMatchingService {

    private final MatchingService matchingService;

    public SemanticMatchingService(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public SemanticMatchResponse analyze(MatchRequest request) {
        MatchResponse baseline = matchingService.match(request);
        List<String> recommendations = new ArrayList<>();

        if (!baseline.missingSkills().isEmpty()) {
            recommendations.add("Prioritize evidence for: " + String.join(", ", baseline.missingSkills()));
        }
        if (baseline.score() < 60) {
            recommendations.add("This role has a meaningful technical gap; target the highest-impact missing requirements first.");
        } else if (baseline.score() < 85) {
            recommendations.add("The profile is reasonably aligned; strengthen project bullets around the missing requirements.");
        } else {
            recommendations.add("Strong technical alignment. Focus interview preparation on architecture, trade-offs, and production ownership.");
        }

        String summary = switch (baseline.score() / 20) {
            case 5, 4 -> "Strong alignment with the recognized technical requirements.";
            case 3 -> "Good alignment with several opportunities to improve positioning.";
            case 2 -> "Partial alignment; important requirements are currently missing.";
            default -> "Low alignment with the recognized technical requirements.";
        };

        return new SemanticMatchResponse(
                "deterministic-v1",
                baseline.score(),
                baseline.matchedSkills(),
                baseline.missingSkills(),
                summary,
                recommendations
        );
    }
}
