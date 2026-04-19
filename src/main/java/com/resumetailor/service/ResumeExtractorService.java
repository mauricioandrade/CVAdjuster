package com.resumetailor.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Service
public class ResumeExtractorService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Extracts plain text from an uploaded resume file.
     * Supports both PDF (.pdf) and Word (.docx) formats.
     *
     * @param file the uploaded resume file
     * @return extracted and cleaned plain text
     * @throws IOException if the file cannot be read
     */
    public String extractText(MultipartFile file) throws IOException {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String filename = originalFilename == null ? "" : originalFilename.toLowerCase(Locale.ROOT);

        log.info("Extracting text from resume file: {}", originalFilename);

        String raw = filename.endsWith(".pdf")
                ? extractFromPdf(file)
                : extractFromDocx(file);

        String cleaned = cleanText(raw);
        log.info("Text extracted successfully. {} characters.", cleaned.length());
        return cleaned;
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("The resume file cannot be empty.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Could not determine the file name.");
        }

        String lower = filename.toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".pdf") && !lower.endsWith(".docx")) {
            throw new IllegalArgumentException(
                    "Unsupported file format. Please upload a PDF (.pdf) or Word (.docx) file.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("The file exceeds the 10MB size limit.");
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";

        return text
                .replaceAll("(\\r?\\n){3,}", "\n\n")
                .lines()
                .map(String::strip)
                .filter(line -> !line.isBlank())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b)
                .strip();
    }
}