package com.vigil.service;

import com.vigil.analyzer.email.EmailSecurityCheck;
import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAnalysisService {

    private final List<EmailSecurityCheck> emailSecurityChecks;
    private final UrlAnalysisService urlAnalysisService;

    // Robust URL regex that handles http/https URLs with paths, query params, etc.
    private static final Pattern URL_PATTERN = Pattern.compile(
        "\\bhttps?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
        Pattern.CASE_INSENSITIVE
    );

    public List<ThreatIndicator> analyzeEmail(EmailScanRequest request) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        // Phase 1: Local email checks
        log.info("Running local security checks for Email subject: {}", request.getSubject());
        emailSecurityChecks.stream()
                .map(check -> check.analyze(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(indicators::add);

        // Phase 2: Extract URLs from body and run URL analysis on each
        List<String> extractedUrls = extractUrls(request.getBody());
        log.info("Extracted {} URLs from the email body.", extractedUrls.size());

        for (String url : extractedUrls) {
            log.info("Running URL analysis on extracted URL: {}", url);
            List<ThreatIndicator> urlIndicators = urlAnalysisService.analyzeUrl(url);
            
            // Append URL indicators to the email indicators
            // We prefix the message so the user knows this came from a link in the email
            for (ThreatIndicator ui : urlIndicators) {
                indicators.add(ThreatIndicator.builder()
                        .type(ui.getType())
                        .severity(ui.getSeverity())
                        .score(ui.getScore())
                        .message("[Found in link: " + url + "] " + ui.getMessage())
                        .source(ui.getSource())
                        .build());
            }
        }

        return indicators;
    }

    private List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return urls;
        }
        
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }
}
