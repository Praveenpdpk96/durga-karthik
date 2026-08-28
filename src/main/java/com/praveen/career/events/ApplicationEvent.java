package com.praveen.career.events;

import com.praveen.career.application.ApplicationStatus;

import java.time.Instant;

public record ApplicationEvent(
        String eventType,
        Long applicationId,
        String company,
        String role,
        ApplicationStatus status,
        Instant occurredAt
) {}
