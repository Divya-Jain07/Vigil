package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Component
public class ExcessiveHyphensCheck implements UrlSecurityCheck {

    private static final int MAX_HYPHENS = 2;

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        try {
            // Add protocol if missing so URI parser works correctly for domain extraction
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String domain = uri.getHost();

            if (domain != null) {
                long hyphenCount = domain.chars().filter(ch -> ch == '-').count();

                if (hyphenCount >= MAX_HYPHENS) {
                    return Optional.of(ThreatIndicator.builder()
                            .type("EXCESSIVE_HYPHENS")
                            .severity(Severity.LOW)
                            .score(10) // Moderate score for phishing attempt
                            .message("The domain contains an excessive number of hyphens (" + hyphenCount + "), which is common in phishing URLs.")
                            .source("Local")
                            .build());
                }
            }
        } catch (URISyntaxException e) {
            // If the URL is malformed, we just skip this check
        }

        return Optional.empty();
    }
}
