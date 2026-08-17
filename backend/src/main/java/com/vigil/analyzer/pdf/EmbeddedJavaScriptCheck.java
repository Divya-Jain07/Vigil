package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmbeddedJavaScriptCheck implements PdfSecurityCheck {

    @Override
    public Optional<ThreatIndicator> check(PdfParseResult result) {
        if (result.isHasJavaScript()) {
            return Optional.of(ThreatIndicator.builder()
                    .type("EMBEDDED_JAVASCRIPT")
                    .message("The PDF contains embedded JavaScript which is highly suspicious and often used for malware delivery.")
                    .severity(Severity.HIGH)
                    .score(70)
                    .source("Local")
                    .build());
        }
        return Optional.empty();
    }
}
