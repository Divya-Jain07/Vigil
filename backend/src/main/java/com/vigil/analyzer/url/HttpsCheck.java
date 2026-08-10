package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HttpsCheck implements UrlSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        if (url != null && url.toLowerCase().startsWith("http://")) {
            return Optional.of(ThreatIndicator.builder()
                    .type("INSECURE_PROTOCOL")
                    .severity(Severity.LOW)
                    .score(20)
                    .message("The URL uses an unencrypted HTTP connection.")
                    .source("Local")
                    .build());
        }
        return Optional.empty();
    }
}
