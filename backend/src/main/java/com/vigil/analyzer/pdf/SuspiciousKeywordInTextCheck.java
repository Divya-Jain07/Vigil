package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SuspiciousKeywordInTextCheck implements PdfSecurityCheck {

    private static final List<String> SUSPICIOUS_PHRASES = List.of(
            "click here", "enter your password", "account suspended",
            "verify your account", "update your billing", "secure your account",
            "immediate action required", "wire transfer", "confidential invoice",
        "act now", "within 24 hours", "verify your identity",
        "avoid permanent", "failure to act", "account will be suspended",
        "account will be locked", "confirm your password", "expire",
        "click the link below", "unusual activity","enable content", "enable macros", "enable editing",
        "this document is protected", "protected document"
    );

    @Override
    public Optional<ThreatIndicator> analyze(PdfParseResult result) {
        if (result.getExtractedText() == null || result.getExtractedText().isBlank()) {
            return Optional.empty();
        }

        String textLower = result.getExtractedText().toLowerCase();
        
        long matches = SUSPICIOUS_PHRASES.stream()
                .filter(textLower::contains)
                .count();

        if (matches > 0) {
            return Optional.of(ThreatIndicator.builder()
                    .type("SUSPICIOUS_TEXT_IN_PDF")
                    .severity(matches > 2 ? Severity.HIGH : Severity.MEDIUM)
                    .score((int) matches * 15)
                    .message("The PDF body contains text patterns common in social engineering or phishing documents (e.g., 'click here', 'verify your account').")
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }
}
