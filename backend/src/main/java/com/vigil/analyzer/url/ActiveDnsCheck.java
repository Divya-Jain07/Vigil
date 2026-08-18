package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Optional;

@Component
public class ActiveDnsCheck implements UrlSecurityCheck {

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();

            if (host != null && !isIpAddress(host)) {
                try {
                    InetAddress[] addresses = InetAddress.getAllByName(host);
                    if (addresses == null || addresses.length == 0) {
                        return createUnresolvedIndicator();
                    }
                } catch (UnknownHostException e) {
                    return createUnresolvedIndicator();
                }
            }
        } catch (Exception e) {
            // Malformed URL, skip
        }

        return Optional.empty();
    }
    
    private boolean isIpAddress(String host) {
        return host.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$");
    }

    private Optional<ThreatIndicator> createUnresolvedIndicator() {
        return Optional.of(ThreatIndicator.builder()
                .type("DEAD_DOMAIN")
                .severity(Severity.LOW)
                .score(10) // Small penalty for a domain that doesn't resolve
                .message("The domain does not resolve to an active IP address. It may be a dead link, parked, or a fake domain.")
                .source("Active DNS")
                .build());
    }
}
