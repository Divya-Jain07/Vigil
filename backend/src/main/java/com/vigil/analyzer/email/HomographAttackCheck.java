package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.IDN;
import java.util.Optional;

@Component
public class HomographAttackCheck implements EmailSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(EmailScanRequest request) {
        if (request.getSender() == null || !request.getSender().contains("@")) {
            return Optional.empty();
        }

        String domain = request.getSender().substring(request.getSender().indexOf('@') + 1);

        try {
            String punycode = IDN.toASCII(domain);
            if (punycode.startsWith("xn--")) {
                return Optional.of(ThreatIndicator.builder()
                        .type("HOMOGRAPH_ATTACK")
                        .message("The sender's domain (" + domain + ") contains non-ASCII characters often used to spoof legitimate domains.")
                        .severity(Severity.CRITICAL)
                        .score(80)
                        .source("Local")
                        .build());
            }
        } catch (IllegalArgumentException e) {
        }

        return Optional.empty();
    }
}
