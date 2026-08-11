package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UrgencyLanguageCheck implements EmailSecurityCheck {

    private static final List<String> URGENCY_PHRASES = List.of(
            "click here immediately", "within 24 hours", "account will be locked", 
            "act now", "immediate action required", "before it's too late", "final notice"
    );

    @Override
    public Optional<ThreatIndicator> analyze(EmailScanRequest request) {
        if (request.getBody() == null || request.getBody().isBlank()) {
            return Optional.empty();
        }

        String bodyLower = request.getBody().toLowerCase();
        
        long matches = URGENCY_PHRASES.stream()
                .filter(bodyLower::contains)
                .count();

        if (matches > 0) {
            return Optional.of(ThreatIndicator.builder()
                    .type("URGENCY_LANGUAGE")
                    .severity(matches > 1 ? Severity.HIGH : Severity.MEDIUM)
                    .score((int) matches * 15)
                    .message("The email body contains phrases intended to create a false sense of urgency, which is a classic social engineering tactic.")
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }
}
