package com.praveen.career.resume;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ResumeDocumentService {
    private static final long MAX_BYTES = 5 * 1024 * 1024;

    public ResumeDocument extract(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a PDF or DOCX resume.");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("Resume file must be 5 MB or smaller.");
        String name = file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename();
        String lower = name.toLowerCase(Locale.ROOT);
        String text;
        String type;
        if (lower.endsWith(".pdf")) {
            type = "PDF";
            try (var document = Loader.loadPDF(file.getBytes())) {
                text = new PDFTextStripper().getText(document);
            }
        } else if (lower.endsWith(".docx")) {
            type = "DOCX";
            try (var document = new XWPFDocument(file.getInputStream())) {
                text = document.getParagraphs().stream().map(XWPFParagraph::getText).collect(Collectors.joining("\n"));
            }
        } else {
            throw new IllegalArgumentException("Only PDF and DOCX resumes are supported.");
        }
        text = text.replace('\u0000', ' ').replaceAll("[\\t ]+", " ").replaceAll("\\n{3,}", "\n\n").trim();
        if (text.length() < 40) throw new IllegalArgumentException("Could not extract enough text from this resume. Scanned-image PDFs are not supported yet.");
        return new ResumeDocument(name, type, text, text.length());
    }

    public record ResumeDocument(String fileName, String fileType, String text, int characters) {}
}
