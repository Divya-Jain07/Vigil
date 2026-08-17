package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.ThreatLevel;
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
            // Convert to punycode. If the result starts with xn--, it means there are non-ASCII characters.
            String punycode = IDN.toASCII(domain);
            if (punycode.startsWith("xn--")) {
                ThreatIndicator threat = new ThreatIndicator(
                        "Homograph Attack Detected",
                        "The sender's domain (" + domain + ") contains non-ASCII characters often used to spoof legitimate domains.",
                        ThreatLevel.CRITICAL
                );
                return Optional.of(threat);
            }
        } catch (IllegalArgumentException e) {
            // Invalid domain format, could also be suspicious
        }

        return Optional.empty();
    }
}
