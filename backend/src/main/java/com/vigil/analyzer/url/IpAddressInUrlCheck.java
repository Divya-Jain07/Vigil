package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class IpAddressInUrlCheck implements UrlSecurityCheck {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$"
    );

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            
            if (host != null && IPV4_PATTERN.matcher(host).matches()) {
                return Optional.of(ThreatIndicator.builder()
                        .type("IP_ADDRESS_URL")
                        .severity(Severity.MEDIUM)
                        .score(40)
                        .message("The URL uses an IP address instead of a domain name.")
                        .source("Local")
                        .build());
            }
        } catch (Exception e) {
            // Invalid URI, skip check
        }
        
        return Optional.empty();
    }
}
