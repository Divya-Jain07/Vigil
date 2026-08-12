package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import java.util.Optional;

public interface PdfSecurityCheck {
    Optional<ThreatIndicator> analyze(PdfParseResult result);
}