package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class SuspiciousTldCheck implements UrlSecurityCheck {

    // Common TLDs often abused by malicious sites
    private static final List<String> SUSPICIOUS_TLDS = List.of(
            ".xyz", ".top", ".club", ".online", ".site", ".tk", ".ml", ".ga", ".cf", ".gq"
    );

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            
            if (host != null) {
                String hostLower = host.toLowerCase();
                for (String tld : SUSPICIOUS_TLDS) {
                    if (hostLower.endsWith(tld)) {
                        return Optional.of(ThreatIndicator.builder()
                                .type("SUSPICIOUS_TLD")
                                .severity(Severity.MEDIUM)
                                .score(30)
                                .message("The URL uses a Top-Level Domain (TLD) commonly associated with malicious activity.")
                                .source("Local")
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            // Invalid URI, skip check
        }
        
        return Optional.empty();
    }
}
