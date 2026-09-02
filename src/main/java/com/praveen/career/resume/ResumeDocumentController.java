package com.praveen.career.resume;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeDocumentController {
    private final ResumeDocumentService service;

    public ResumeDocumentController(ResumeDocumentService service) { this.service = service; }

    @PostMapping(value = "/extract", consumes = "multipart/form-data")
    public ResumeDocumentService.ResumeDocument extract(@RequestPart("file") MultipartFile file) throws IOException {
        return service.extract(file);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalid(IllegalArgumentException exception) { return new ErrorResponse(exception.getMessage()); }

    public record ErrorResponse(String message) {}
}
