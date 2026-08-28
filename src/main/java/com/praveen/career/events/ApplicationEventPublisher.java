package com.praveen.career.events;

import com.praveen.career.application.JobApplication;

public interface ApplicationEventPublisher {
    void applicationCreated(JobApplication application);
    void applicationStatusChanged(JobApplication application);
}
