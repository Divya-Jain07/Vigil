package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SuspiciousSubjectCheck implements EmailSecurityCheck {

    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "urgent", "action required", "password", "invoice", "account suspended", 
            "verify your account", "security alert", "payment declined", "won", "lottery"
    );

    @Override
    public Optional<ThreatIndicator> analyze(EmailScanRequest request) {
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            return Optional.empty();
        }

        String subjectLower = request.getSubject().toLowerCase();
        
        long matches = SUSPICIOUS_KEYWORDS.stream()
                .filter(subjectLower::contains)
                .count();

        if (matches > 0) {
            return Optional.of(ThreatIndicator.builder()
                    .type("SUSPICIOUS_SUBJECT")
                    .severity(matches > 1 ? Severity.MEDIUM : Severity.LOW)
                    .score((int) matches * 10)
                    .message("The email subject contains common phishing or spam keywords (e.g., urgent, invoice, password).")
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }
}
