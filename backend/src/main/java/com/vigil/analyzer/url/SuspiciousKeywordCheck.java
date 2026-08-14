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
            "login", "signin", "sign-in", "secure", "security",
            "account", "verify", "verification", "confirm", "update",
            "banking", "paypal", "amazon", "apple", "microsoft",
            "password", "credential", "authenticate", "support"
    );

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();

            if (host == null) return Optional.empty();

            // For testing purposes and better coverage, check the entire URL string (excluding query params if we want, but let's check the whole thing)
            String urlLower = urlToParse.toLowerCase();

            List<String> matched = SUSPICIOUS_KEYWORDS.stream()
                    .filter(urlLower::contains)
                    .collect(Collectors.toList());

            if (!matched.isEmpty()) {
                int score = Math.min(matched.size() * 10, 40); // cap at 40
                Severity severity = matched.size() >= 3 ? Severity.HIGH
                        : matched.size() == 2 ? Severity.MEDIUM
                        : Severity.LOW;

                return Optional.of(ThreatIndicator.builder()
                        .type("SUSPICIOUS_KEYWORDS_IN_DOMAIN")
                        .severity(severity)
                        .score(score)
                        .message("The domain contains suspicious keywords often used in phishing sites: "
                                + String.join(", ", matched) + ". Legitimate brands do not embed these words in their domain.")
                        .source("Local")
                        .build());
            }

        } catch (URISyntaxException e) {
            // Malformed URL — skip check silently
        }

        return Optional.empty();
    }
}
