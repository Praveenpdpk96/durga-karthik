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
        String company = request.company().trim();
        String role = request.role().trim();
        if (repository.existsByCompanyIgnoreCaseAndRoleIgnoreCase(company, role)) {
            throw new DuplicateApplicationException(company, role);
        }
        ApplicationStatus status = request.status() == null ? ApplicationStatus.APPLIED : request.status();
        JobApplication saved = repository.save(new JobApplication(company, role, normalizeUrl(request.jobUrl()), status));
        eventPublisher.applicationCreated(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> findAll() {
        return repository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional
    public JobApplication update(Long id, UpdateApplicationRequest request) {
        JobApplication application = findById(id);
        String company = request.company().trim();
        String role = request.role().trim();
        if (repository.existsByCompanyIgnoreCaseAndRoleIgnoreCaseAndIdNot(company, role, id)) {
            throw new DuplicateApplicationException(company, role);
        }
        application.updateDetails(company, role, normalizeUrl(request.jobUrl()));
        return application;
    }

    @Transactional
    public JobApplication updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = findById(id);
        application.updateStatus(status);
        eventPublisher.applicationStatusChanged(application);
        return application;
    }

    @Transactional
    public void delete(Long id) {
        JobApplication application = findById(id);
        repository.delete(application);
    }

    private JobApplication findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    private String normalizeUrl(String jobUrl) {
        return jobUrl == null || jobUrl.isBlank() ? null : jobUrl.trim();
    }
}
