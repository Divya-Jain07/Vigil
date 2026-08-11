package com.vigil.service;

import com.vigil.analyzer.pdf.PdfParseResult;
import com.vigil.analyzer.pdf.PdfSecurityCheck;
import com.vigil.model.ThreatIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfAnalysisService {

    private final List<PdfSecurityCheck> pdfSecurityChecks;
    private final UrlAnalysisService urlAnalysisService;

    public List<ThreatIndicator> analyzePdf(PdfParseResult result) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        // Phase 1: Local PDF checks
        log.info("Running local security checks for PDF: {}", result.getFileName());
        pdfSecurityChecks.stream()
                .map(check -> check.analyze(result))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(indicators::add);

        // Phase 2: Run URL analysis on each extracted URL
        List<String> extractedUrls = result.getExtractedUrls();
        log.info("Extracted {} URLs from the PDF.", extractedUrls.size());

        for (String url : extractedUrls) {
            log.info("Running URL analysis on extracted URL: {}", url);
            List<ThreatIndicator> urlIndicators = urlAnalysisService.analyzeUrl(url);
            
            // Append URL indicators to the PDF indicators
            for (ThreatIndicator ui : urlIndicators) {
                indicators.add(ThreatIndicator.builder()
                        .type(ui.getType())
                        .severity(ui.getSeverity())
                        .score(ui.getScore())
                        .message("[Found in PDF link: " + url + "] " + ui.getMessage())
                        .source(ui.getSource())
                        .build());
            }
        }

        return indicators;
    }
}
