package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SubdomainEntropyCheck implements UrlSecurityCheck {

    private static final double ENTROPY_THRESHOLD = 4.0;

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();
            if (host == null) {
                return Optional.empty();
            }

            String[] parts = host.split("\\.");
            if (parts.length > 2) {
                StringBuilder subdomainBuilder = new StringBuilder();
                for (int i = 0; i < parts.length - 2; i++) {
                    subdomainBuilder.append(parts[i]);
                }
                String subdomain = subdomainBuilder.toString();
                
                if (!subdomain.isEmpty()) {
                    double entropy = calculateShannonEntropy(subdomain);
                    if (entropy > ENTROPY_THRESHOLD) {
                        return Optional.of(ThreatIndicator.builder()
                                .type("HIGH_SUBDOMAIN_ENTROPY")
                                .message("The subdomain string appears to be randomly generated (Entropy: " + String.format("%.2f", entropy) + "), which is characteristic of Algorithmically Generated Domains (DGA).")
                                .severity(Severity.HIGH)
                                .score(25)
                                .source("Local")
                                .build());
                    }
                }
            }
        } catch (URISyntaxException e) {
            // Skip
        }
        return Optional.empty();
    }

    private double calculateShannonEntropy(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        Map<Character, Integer> charCounts = new HashMap<>();
        for (char c : s.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }
        double entropy = 0.0;
        int length = s.length();
        for (int count : charCounts.values()) {
            double prob = (double) count / length;
            entropy -= prob * (Math.log(prob) / Math.log(2));
        }
        return entropy;
    }
}
