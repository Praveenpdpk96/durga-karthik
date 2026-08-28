package com.praveen.career.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public JobApplication create(CreateApplicationRequest request) {
        ApplicationStatus status = request.status() == null ? ApplicationStatus.APPLIED : request.status();
        return repository.save(new JobApplication(request.company(), request.role(), request.jobUrl(), status));
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
        return application;
    }
}
