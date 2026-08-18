package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks if the URL's domain or path contains keywords commonly found in phishing URLs.
 * Legitimate companies like PayPal or your bank don't need to put "login", "verify",
 * or "secure" as keywords inside their domain name.
 */
@Component
public class SuspiciousKeywordCheck implements UrlSecurityCheck {

    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "login.php", "account-update", "verify", "secure", "security",
            "account", "verification", "confirm", "update", "credential", "authenticate"
    );

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String path = uri.getPath();
            String query = uri.getQuery();
            
            String pathAndQuery = (path != null ? path : "") + (query != null ? query : "");
            
            if (pathAndQuery.isEmpty()) {
                return Optional.empty();
            }

            String pathLower = pathAndQuery.toLowerCase();

            List<String> matched = SUSPICIOUS_KEYWORDS.stream()
                    .filter(pathLower::contains)
                    .collect(Collectors.toList());

            if (!matched.isEmpty()) {
                return Optional.of(ThreatIndicator.builder()
                        .type("SUSPICIOUS_KEYWORDS_IN_PATH")
                        .severity(Severity.LOW)
                        .score(15)
                        .message("The URL path contains suspicious keywords: "
                                + String.join(", ", matched) + ". This often directs users to credential harvesting pages.")
                        .source("Local")
                        .build());
            }

        } catch (URISyntaxException e) {
            // Malformed URL
        }

        return Optional.empty();
    }
}
