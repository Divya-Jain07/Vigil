package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class BrandImpersonationCheck implements UrlSecurityCheck {

    private static final List<String> BRANDS = List.of(
            "paypal", "bank", "amazon", "apple", "microsoft", "google", "github", "facebook"
    );

    @Override
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();

            if (host != null) {
                String hostLower = host.toLowerCase();

                for (String brand : BRANDS) {
                    if (hostLower.contains(brand)) {
                        return createIndicator(brand, "exact substring match");
                    }
                    
                    // Typosquatting Check: Levenshtein distance <= 2
                    // Check parts of the domain (split by dots and hyphens)
                    String[] tokens = hostLower.split("[\\.-]");
                    for (String token : tokens) {
                        // Only calculate if lengths are somewhat close
                        if (Math.abs(token.length() - brand.length()) <= 2) {
                            if (calculateLevenshteinDistance(token, brand) <= 2) {
                                return createIndicator(brand, "typosquatting on '" + token + "'");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Invalid URI
        }

        return Optional.empty();
    }
    
    private Optional<ThreatIndicator> createIndicator(String brand, String reason) {
        return Optional.of(ThreatIndicator.builder()
                .type("BRAND_IMPERSONATION")
                .severity(Severity.HIGH)
                .score(30)
                .message("The domain appears to impersonate '" + brand + "' (" + reason + ") but is not the verified brand domain.")
                .source("Local")
                .build());
    }

    private int calculateLevenshteinDistance(String lhs, String rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++) distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++) distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++) {
            for (int j = 1; j <= rhs.length(); j++) {
                int cost = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(Math.min(
                                distance[i - 1][j] + 1,
                                distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost);
            }
        }
        return distance[lhs.length()][rhs.length()];
    }
}
