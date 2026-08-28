package com.praveen.career.application;

import jakarta.validation.constraints.NotBlank;

public record CreateApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        String jobUrl,
        ApplicationStatus status
) {}
