package com.praveen.career.events;

import com.praveen.career.application.JobApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(ApplicationEventPublisher.class)
public class NoOpApplicationEventPublisher implements ApplicationEventPublisher {
    @Override
    public void applicationCreated(JobApplication application) {
        // Event publishing is intentionally disabled for local/test environments.
    }

    @Override
    public void applicationStatusChanged(JobApplication application) {
        // Event publishing is intentionally disabled for local/test environments.
    }
}
