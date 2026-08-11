package com.vigil.controller;

import com.vigil.dto.UrlScanRequest;
import com.vigil.dto.UrlScanResponse;
import com.vigil.model.Scan;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import com.vigil.repository.ScanRepository;
import com.vigil.service.GeminiExplanationService;
import com.vigil.service.ThreatScoringService;
import com.vigil.service.UrlAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class UrlScanController {

    private final UrlAnalysisService urlAnalysisService;
    private final ThreatScoringService threatScoringService;
    private final GeminiExplanationService geminiExplanationService;
    private final ScanRepository scanRepository;

    @PostMapping("/url")
    public ResponseEntity<UrlScanResponse> scanUrl(@Valid @RequestBody UrlScanRequest request) {

        // Step 1: Run local + external analysis
        List<ThreatIndicator> indicators = urlAnalysisService.analyzeUrl(request.getUrl());

        // Step 2: Calculate threat score
        ThreatScore score = threatScoringService.calculateScore(indicators);

        // Step 3: Generate Gemini explanation (falls back to rule-based text if unavailable)
        String explanation = geminiExplanationService.explain(request.getUrl(), score, indicators);

        // Step 4: Persist the full scan result
        Scan scan = Scan.builder()
                .url(request.getUrl())
                .threatScore(score)
                .indicators(indicators)
                .explanation(explanation)
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();

        Scan savedScan = scanRepository.save(scan);

        // Step 5: Build and return API response
        UrlScanResponse.ScanData scanData = UrlScanResponse.ScanData.builder()
                .id(savedScan.getId())
                .url(savedScan.getUrl())
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
