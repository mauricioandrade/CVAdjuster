package com.resumetailor.controller;

import com.resumetailor.dto.TailorResponse;
import com.resumetailor.service.ResumeTailorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ResumeTailorController {

    private final ResumeTailorService resumeTailorService;

    /** Página principal */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /** Processa o currículo via formulário web */
    @PostMapping("/tailor")
    public String tailorResume(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "outputFormat", defaultValue = "pdf") String outputFormat,
            HttpServletRequest httpRequest,
            Model model) {

        log.info("Recebida requisição de adaptação. Arquivo: {}, Formato: {}",
                resumeFile.getOriginalFilename(), outputFormat);

        if (jobDescription == null || jobDescription.trim().isBlank()) {
            model.addAttribute("error", "The job description cannot be empty.");
            return "index";
        }

        TailorResponse response = resumeTailorService.tailorResume(resumeFile, jobDescription, outputFormat);

        if (response.isSuccess()) {
            // Monta a URL pública base para o Google Docs importar o DOCX
            String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName()
                    + (httpRequest.getServerPort() != 80 && httpRequest.getServerPort() != 443
                    ? ":" + httpRequest.getServerPort() : "");

            String docxPublicUrl = baseUrl + response.getDocxDownloadUrl();

            model.addAttribute("success", true);
            model.addAttribute("tailoredText", response.getTailoredResumeText());
            model.addAttribute("downloadPdfUrl", response.getDownloadUrl().endsWith(".pdf")
                    ? response.getDownloadUrl()
                    : response.getDocxDownloadUrl().replace(".docx", ".pdf"));
            model.addAttribute("downloadDocxUrl", response.getDocxDownloadUrl());
            model.addAttribute("googleDocsUrl",
                    "https://docs.google.com/viewer?url=" + java.net.URLEncoder.encode(docxPublicUrl, java.nio.charset.StandardCharsets.UTF_8) + "&embedded=false");
            model.addAttribute("googleDocsEditUrl",
                    "https://docs.google.com/document/create?usp=pp_url&title=Curriculo+Adaptado");
            model.addAttribute("docxPublicUrl", docxPublicUrl);
            model.addAttribute("format", response.getFormat());
            model.addAttribute("changes", response.getChanges());
        } else {
            model.addAttribute("error", response.getMessage());
        }

        return "result";
    }

    /** Download do arquivo gerado */
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.badRequest().build();
            }

            byte[] fileBytes = resumeTailorService.getGeneratedFile(filename);

            String contentType = filename.endsWith(".docx")
                    ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    : "application/pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileBytes);

        } catch (IOException e) {
            log.error("Erro ao baixar arquivo: {}", filename, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Redireciona para o Google Docs com o DOCX importado.
     * O Google Docs aceita importar via URL pública no formato:
     * https://docs.google.com/viewer?url=<URL_DO_DOCX>
     *
     * Para EDIÇÃO real, o usuário precisa estar logado no Google e usar
     * "Abrir com Google Docs" no Google Drive — orientamos isso na UI.
     */
    @GetMapping("/open-in-gdocs/{filename}")
    public ResponseEntity<Void> openInGoogleDocs(
            @PathVariable String filename,
            HttpServletRequest request) {

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                ? ":" + request.getServerPort() : "");

        String docxUrl = baseUrl + "/download/" + filename;
        String encodedUrl = java.net.URLEncoder.encode(docxUrl, java.nio.charset.StandardCharsets.UTF_8);

        // Google Docs viewer — abre para leitura e o usuário pode clicar em "Open with Google Docs"
        String googleDocsUrl = "https://docs.google.com/viewer?url=" + encodedUrl;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(googleDocsUrl))
                .build();
    }

    /** API REST para integração futura */
    @PostMapping("/api/tailor")
    @ResponseBody
    public ResponseEntity<TailorResponse> tailorResumeApi(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "outputFormat", defaultValue = "pdf") String outputFormat,
            HttpServletRequest httpRequest) {

        TailorResponse response = resumeTailorService.tailorResume(resumeFile, jobDescription, outputFormat);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
