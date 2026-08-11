package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmbeddedUrlCheck implements PdfSecurityCheck {

    private static final int MAX_URLS_THRESHOLD = 15;

    @Override
    public Optional<ThreatIndicator> analyze(PdfParseResult result) {
        if (result.getExtractedUrls() == null || result.getExtractedUrls().isEmpty()) {
            return Optional.empty();
        }

        int urlCount = result.getExtractedUrls().size();
        
        if (urlCount > MAX_URLS_THRESHOLD) {
            return Optional.of(ThreatIndicator.builder()
                    .type("EXCESSIVE_EMBEDDED_URLS")
                    .severity(Severity.MEDIUM)
                    .score(25)
                    .message("The PDF contains an unusually high number of embedded URLs (" + urlCount + "), which could indicate a spam or redirect document.")
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }
}
