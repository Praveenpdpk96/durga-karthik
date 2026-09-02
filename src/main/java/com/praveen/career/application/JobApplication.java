package com.praveen.career.application;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    private String jobUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected JobApplication() {}

    public JobApplication(String company, String role, String jobUrl, ApplicationStatus status) {
        this.company = company;
        this.role = role;
        this.jobUrl = jobUrl;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public String getJobUrl() { return jobUrl; }
    public ApplicationStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateDetails(String company, String role, String jobUrl) {
        this.company = company;
        this.role = role;
        this.jobUrl = jobUrl;
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }
}
