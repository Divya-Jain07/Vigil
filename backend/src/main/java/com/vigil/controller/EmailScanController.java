package com.vigil.controller;

import com.vigil.dto.EmailScanRequest;
import com.vigil.dto.UrlScanResponse;
import com.vigil.model.Scan;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import com.vigil.repository.ScanRepository;
import com.vigil.repository.UserRepository;
import com.vigil.service.EmailAnalysisService;
import com.vigil.service.GeminiExplanationService;
import com.vigil.service.ThreatScoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class EmailScanController {

    private final EmailAnalysisService emailAnalysisService;
    private final ThreatScoringService threatScoringService;
    private final GeminiExplanationService geminiExplanationService;
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;

    @PostMapping("/email")
    public ResponseEntity<UrlScanResponse> scanEmail(@Valid @RequestBody EmailScanRequest request) {

        // Step 1: Run local email analysis and extract URLs for scanning
        List<ThreatIndicator> indicators = emailAnalysisService.analyzeEmail(request);

        // Step 2: Calculate threat score
        ThreatScore score = threatScoringService.calculateScore(indicators);

        // Step 3: Generate Gemini explanation
        String explanation = geminiExplanationService.explainEmail(request.getSubject(), score, indicators);

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
        
        // Step 4: Persist the full scan result
        Scan scan = Scan.builder()
                .userId(userId)
                .inputType("EMAIL")
                .emailSubject(request.getSubject())
                .emailSender(request.getSender())
                .threatScore(score)
                .indicators(indicators)
                .explanation(explanation)
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();

        Scan savedScan = scanRepository.save(scan);

        // Step 5: Build and return API response
        // Reusing UrlScanResponse format, though the original 'url' field might be null or "Email Scan"
        UrlScanResponse.ScanData scanData = UrlScanResponse.ScanData.builder()
                .id(savedScan.getId())
                .url(request.getSubject() != null ? "Email: " + request.getSubject() : "Email Scan")
                .score(score.getScore())
                .severity(score.getSeverity().name())
                .indicators(indicators)
                .status(savedScan.getStatus())
                .explanation(explanation)
                .build();

        UrlScanResponse response = UrlScanResponse.builder()
                .success(true)
                .data(scanData)
                .build();

        return ResponseEntity.ok(response);
    }
}
