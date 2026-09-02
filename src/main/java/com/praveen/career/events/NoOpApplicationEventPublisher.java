package com.praveen.career.events;

import com.praveen.career.application.JobApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "career.events.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class NoOpApplicationEventPublisher implements ApplicationEventPublisher {
    @Override
    public void applicationCreated(JobApplication application) {
        // Event publishing is intentionally disabled when Kafka events are not enabled.
    }

    @Override
    public void applicationStatusChanged(JobApplication application) {
        // Event publishing is intentionally disabled when Kafka events are not enabled.
    }
}
