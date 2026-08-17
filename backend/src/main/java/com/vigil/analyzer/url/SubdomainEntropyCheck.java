package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.ThreatLevel;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SubdomainEntropyCheck implements UrlSecurityCheck {

    // Threshold for Shannon entropy. Domains with high entropy look like random strings (e.g. DGA).
    private static final double ENTROPY_THRESHOLD = 4.0;

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return Optional.empty();
            }

            // Extract subdomain (everything before the last two parts)
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
                        return Optional.of(new ThreatIndicator(
                                "High Subdomain Entropy",
                                "The subdomain string appears to be randomly generated (Entropy: " + String.format("%.2f", entropy) + "), which is characteristic of Algorithmically Generated Domains (DGA).",
                                ThreatLevel.HIGH
                        ));
                    }
                }
            }
        } catch (URISyntaxException e) {
            // Ignored for this check
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
