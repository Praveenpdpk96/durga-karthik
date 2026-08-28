package com.praveen.career.application;

import com.praveen.career.events.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private JobApplicationService service;

    @Test
    void createsApplicationAndPublishesEvent() {
        JobApplication saved = new JobApplication("Acme", "Software Engineer", null, ApplicationStatus.APPLIED);
        when(repository.save(any(JobApplication.class))).thenReturn(saved);

        JobApplication result = service.create(new CreateApplicationRequest("Acme", "Software Engineer", null, null));

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        verify(eventPublisher).applicationCreated(saved);
    }

    @Test
    void updatesStatusAndPublishesEvent() {
        JobApplication application = new JobApplication("Acme", "Software Engineer", null, ApplicationStatus.APPLIED);
        when(repository.findById(10L)).thenReturn(Optional.of(application));

        JobApplication result = service.updateStatus(10L, ApplicationStatus.INTERVIEW);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.INTERVIEW);
        verify(eventPublisher).applicationStatusChanged(application);
    }
}
