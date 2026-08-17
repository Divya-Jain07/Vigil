package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Component
public class HomographUrlCheck implements UrlSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return Optional.empty();
            }

            String punycode = IDN.toASCII(host);
            if (punycode.startsWith("xn--") || punycode.contains(".xn--")) {
                return Optional.of(ThreatIndicator.builder()
                        .type("IDN_HOMOGRAPH_ATTACK")
                        .message("The URL domain uses Internationalized Domain Name (IDN) characters (Punycode: " + punycode + "), which is often used to visually spoof legitimate domains.")
                        .severity(Severity.CRITICAL)
                        .score(90)
                        .source("Local")
                        .build());
            }
        } catch (URISyntaxException | IllegalArgumentException e) {
        }
        return Optional.empty();
    }
}
