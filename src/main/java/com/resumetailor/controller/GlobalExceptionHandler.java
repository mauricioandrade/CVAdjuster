package com.resumetailor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException e, Model model) {
        log.warn("File too large: {}", e.getMessage());
        model.addAttribute("error", "The uploaded file is too large. The limit is 10MB.");
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        log.error("Unhandled error", e);
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        return "index";
    }
}
