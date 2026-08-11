package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import java.util.Optional;

public interface PdfSecurityCheck {
    /**
     * Analyzes a parsed PDF for potential security threats.
     *
     * @param result The parsed PDF data to check.
     * @return An Optional ThreatIndicator if a threat is found, or empty otherwise.
     */
    Optional<ThreatIndicator> analyze(PdfParseResult result);
}
