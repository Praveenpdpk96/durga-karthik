package com.praveen.career.matching;

import java.util.Set;

public record MatchResponse(
        int score,
        Set<String> matchedSkills,
        Set<String> missingSkills
) {}
