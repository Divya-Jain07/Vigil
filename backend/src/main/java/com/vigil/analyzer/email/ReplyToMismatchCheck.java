package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReplyToMismatchCheck implements EmailSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(EmailScanRequest request) {
        if (request.getReplyTo() == null || request.getReplyTo().isBlank()) {
            return Optional.empty();
        }

        String senderDomain = extractDomain(request.getSender());
        String replyToDomain = extractDomain(request.getReplyTo());

        if (senderDomain != null && replyToDomain != null && !senderDomain.equalsIgnoreCase(replyToDomain)) {
            return Optional.of(ThreatIndicator.builder()
                    .type("REPLY_TO_MISMATCH")
                    .severity(Severity.HIGH)
                    .score(40)
                    .message(String.format("The sender domain (%s) does not match the reply-to domain (%s). This is a common spoofing technique.", senderDomain, replyToDomain))
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }

    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        String[] parts = email.split("@");
        if (parts.length == 2) {
            String domainPart = parts[1].trim();
            // Remove any trailing generic characters like > if format is "Name <email@domain.com>"
            if (domainPart.endsWith(">")) {
                domainPart = domainPart.substring(0, domainPart.length() - 1);
            }
            return domainPart.toLowerCase();
        }
        return null;
    }
}
