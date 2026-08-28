package com.praveen.career.application;

import com.praveen.career.events.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public JobApplicationService(JobApplicationRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public JobApplication create(CreateApplicationRequest request) {
        ApplicationStatus status = request.status() == null ? ApplicationStatus.APPLIED : request.status();
        JobApplication saved = repository.save(new JobApplication(request.company(), request.role(), request.jobUrl(), status));
        eventPublisher.applicationCreated(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findAll() {
        return repository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional
    public JobApplication updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        application.updateStatus(status);
        eventPublisher.applicationStatusChanged(application);
        return application;
    }
}
