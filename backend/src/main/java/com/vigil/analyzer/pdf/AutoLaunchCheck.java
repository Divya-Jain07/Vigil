package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutoLaunchCheck implements PdfSecurityCheck {

    @Override
    public Optional<ThreatIndicator> check(PdfParseResult result) {
        if (result.isHasOpenAction()) {
            return Optional.of(new ThreatIndicator(
                    "Auto-Launch Action",
                    "The PDF contains an OpenAction that executes automatically when the document is opened.",
                    ThreatLevel.HIGH
            ));
        }
        return Optional.empty();
    }
}
