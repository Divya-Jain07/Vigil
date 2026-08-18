package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;

@Component
public class DeepSubdomainStackingCheck implements UrlSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();

            if (host != null) {
                long dotCount = host.chars().filter(ch -> ch == '.').count();

                if (dotCount >= 3) {
                    return Optional.of(ThreatIndicator.builder()
                            .type("DEEP_SUBDOMAIN_STACKING")
                            .severity(Severity.MEDIUM)
                            .score(20)
                            .message("The domain has 3 or more dots, which is often used to hide the real domain on mobile browsers.")
                            .source("Local")
                            .build());
                }
            }
        } catch (Exception e) {
            // Invalid URI
        }

        return Optional.empty();
    }
}
