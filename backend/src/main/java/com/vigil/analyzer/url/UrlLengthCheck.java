package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlLengthCheck implements UrlSecurityCheck {

    private static final int MAX_LENGTH = 150;

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        if (url != null && url.length() > MAX_LENGTH) {
            return Optional.of(ThreatIndicator.builder()
                    .type("EXCESSIVE_URL_LENGTH")
                    .severity(Severity.LOW)
                    .score(10)
                    .message("The URL is exceptionally long, which may be an attempt to obscure its true destination.")
                    .source("Local")
                    .build());
        }
        return Optional.empty();
    }
}
