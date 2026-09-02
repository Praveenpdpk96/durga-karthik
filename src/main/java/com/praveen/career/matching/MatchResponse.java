package com.praveen.career.matching;

import java.util.Set;

public record MatchResponse(
        int score,
        Set<String> matchedSkills,
        Set<String> missingSkills,
        Set<String> requiredSkills,
        Set<String> preferredSkills,
        int technicalCoverage,
        int experienceFit,
        int roleFit,
        int evidenceCount
) {}
