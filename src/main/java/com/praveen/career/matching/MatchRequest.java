package com.praveen.career.matching;

import jakarta.validation.constraints.NotBlank;

public record MatchRequest(
        @NotBlank String resumeText,
        @NotBlank String jobDescription
) {}
