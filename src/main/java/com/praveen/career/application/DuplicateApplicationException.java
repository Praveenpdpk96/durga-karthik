package com.praveen.career.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String company, String role) {
        super("An application for " + role + " at " + company + " already exists.");
    }
}
