package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import java.util.Optional;

public interface EmailSecurityCheck {
    /**
     * Analyzes an email for potential security threats.
     *
     * @param request The email data to check.
     * @return An Optional ThreatIndicator if a threat is found, or empty otherwise.
     */
    Optional<ThreatIndicator> analyze(EmailScanRequest request);
}
