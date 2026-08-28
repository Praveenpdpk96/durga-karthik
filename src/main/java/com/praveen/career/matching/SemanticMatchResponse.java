package com.praveen.career.matching;

import java.util.List;
import java.util.Set;

public record SemanticMatchResponse(
        String engine,
        int score,
        Set<String> matchedSkills,
        Set<String> missingSkills,
        String summary,
        List<String> recommendations
) {}
