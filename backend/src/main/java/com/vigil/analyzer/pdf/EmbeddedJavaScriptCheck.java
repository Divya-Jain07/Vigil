package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmbeddedJavaScriptCheck implements PdfSecurityCheck {

    @Override
    public Optional<ThreatIndicator> check(PdfParseResult result) {
        if (result.isHasJavaScript()) {
            return Optional.of(new ThreatIndicator(
                    "Embedded JavaScript",
                    "The PDF contains embedded JavaScript which is highly suspicious and often used for malware delivery.",
                    ThreatLevel.HIGH
            ));
        }
        return Optional.empty();
    }
}
