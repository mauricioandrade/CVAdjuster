package com.resumetailor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailorResponse {
    private boolean success;
    private String message;
    private String tailoredResumeText;
    private String downloadUrl;      // PDF
    private String docxDownloadUrl;  // DOCX (sempre gerado)
    private String format;
    private List<ChangeHighlight> changes;
}
