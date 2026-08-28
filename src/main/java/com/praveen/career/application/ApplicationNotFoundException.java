package com.praveen.career.application;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) {
        super("Job application not found: " + id);
    }
}
