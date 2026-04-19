package com.resumetailor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailorRequest {
    private String resumeText;
    private String jobDescription;
    private String outputFormat; // "pdf" or "docx"
}
