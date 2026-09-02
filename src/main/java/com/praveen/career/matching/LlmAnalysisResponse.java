package com.praveen.career.matching;

import java.util.List;

public record LlmAnalysisResponse(
        String engine,
        String model,
        boolean aiEnhanced,
        int baselineScore,
        String fitAssessment,
        String seniorityAssessment,
        String domainAssessment,
        List<String> strengths,
        List<String> gaps,
        List<String> resumeImprovements,
        List<String> interviewFocus,
        String note
) {}
