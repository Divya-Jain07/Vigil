package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutoLaunchCheck implements PdfSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(PdfParseResult result) {
        if (result.isHasOpenAction()) {
            return Optional.of(ThreatIndicator.builder()
                    .type("AUTO_LAUNCH_ACTION")
                    .message("The PDF contains an OpenAction that executes automatically when the document is opened.")
                    .severity(Severity.HIGH)
                    .score(60)
                    .source("Local")
                    .build());
        }
        return Optional.empty();
    }
}
