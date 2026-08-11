package com.vigil.controller;

import com.vigil.analyzer.pdf.PdfParseResult;
import com.vigil.analyzer.pdf.PdfParser;
import com.vigil.dto.UrlScanResponse;
import com.vigil.model.Scan;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import com.vigil.repository.ScanRepository;
import com.vigil.repository.UserRepository;
import com.vigil.service.GeminiExplanationService;
import com.vigil.service.PdfAnalysisService;
import com.vigil.service.ThreatScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class PdfScanController {

    private final PdfParser pdfParser;
    private final PdfAnalysisService pdfAnalysisService;
    private final ThreatScoringService threatScoringService;
    private final GeminiExplanationService geminiExplanationService;
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UrlScanResponse> scanPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return buildErrorResponse("File is empty", HttpStatus.BAD_REQUEST);
        }

        if (!"application/pdf".equals(file.getContentType())) {
            return buildErrorResponse("File must be a PDF", HttpStatus.BAD_REQUEST);
        }

        try {
            // Step 1: Parse PDF
            log.info("Parsing uploaded PDF: {}", file.getOriginalFilename());
            PdfParseResult parseResult = pdfParser.parse(file.getInputStream(), file.getOriginalFilename());

            // Step 2: Run Analysis
            List<ThreatIndicator> indicators = pdfAnalysisService.analyzePdf(parseResult);

            // Step 3: Calculate threat score
            ThreatScore score = threatScoringService.calculateScore(indicators);

            // Step 4: Generate Gemini explanation
            String explanation = geminiExplanationService.explainPdf(file.getOriginalFilename(), score, indicators);

            // Determine if user is authenticated
            String userId = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
                
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.User) {
                    String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                    userId = userRepository.findByEmail(email).map(com.vigil.model.User::getId).orElse(null);
                }
            }
            
            // Step 5: Persist the full scan result
            Scan scan = Scan.builder()
                    .userId(userId)
                    .inputType("PDF")
                    .fileName(file.getOriginalFilename())
                    .threatScore(score)
                    .indicators(indicators)
                    .explanation(explanation)
                    .status("COMPLETED")
                    .createdAt(LocalDateTime.now())
                    .build();

            Scan savedScan = scanRepository.save(scan);

            // Step 6: Build and return API response
            UrlScanResponse.ScanData scanData = UrlScanResponse.ScanData.builder()
                    .id(savedScan.getId())
                    .url(file.getOriginalFilename() != null ? "PDF: " + file.getOriginalFilename() : "PDF Scan")
                    .score(score.getScore())
                    .severity(score.getSeverity().name())
                    .indicators(indicators)
                    .status(savedScan.getStatus())
                    .explanation(explanation)
                    .build();

            return ResponseEntity.ok(UrlScanResponse.builder()
                    .success(true)
                    .data(scanData)
                    .build());

        } catch (IOException e) {
            log.error("Failed to parse PDF", e);
            return buildErrorResponse("Failed to process the uploaded PDF file. It may be corrupted or encrypted.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private ResponseEntity<UrlScanResponse> buildErrorResponse(String message, HttpStatus status) {
        UrlScanResponse response = UrlScanResponse.builder()
                .success(false)
                .data(UrlScanResponse.ScanData.builder()
                        .status("FAILED")
                        .explanation(message)
                        .build())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
