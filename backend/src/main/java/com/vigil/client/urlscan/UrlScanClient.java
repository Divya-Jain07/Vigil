package com.vigil.client.urlscan;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client for the URLScan.io API.
 *
 * Searches the URLScan.io database for existing scan results for a given URL
 * and retrieves the verdict (malicious flag and threat categories).
 *
 * Docs: https://urlscan.io/docs/api/
 */
@Slf4j
@Component
public class UrlScanClient {

    private static final String SOURCE = "URLScan.io";

    private final RestClient restClient;
    private final String apiKey;

    public UrlScanClient(
            @Qualifier("urlScanRestClient") RestClient restClient,
            @Value("${vigil.urlscan.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    /**
     * Searches URLScan.io for existing analysis results for a URL.
     *
     * @param url The URL to check.
     * @return An Optional ThreatIndicator if a malicious verdict is found, or empty otherwise.
     */
    @SuppressWarnings("unchecked")
    public Optional<ThreatIndicator> analyze(String url) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("URLScan.io API key not configured. Skipping external check.");
            return Optional.empty();
        }

        try {
            Map<String, Object> response = restClient.get()
                    .uri("/search/?q=page.url:\"{url}\"&size=1", url)
                    .header("API-Key", apiKey)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return Optional.empty();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty()) {
                log.info("URLScan.io: No existing scan results found for URL.");
                return Optional.empty();
            }

            // Take the most recent result
            Map<String, Object> latestResult = results.get(0);
            Map<String, Object> verdicts = (Map<String, Object>) latestResult.get("verdicts");
            if (verdicts == null) return Optional.empty();

            Map<String, Object> overall = (Map<String, Object>) verdicts.get("overall");
            if (overall == null) return Optional.empty();

            Boolean malicious = (Boolean) overall.get("malicious");
            Number score = (Number) overall.get("score");
            List<String> categories = (List<String>) overall.get("categories");

            if (Boolean.TRUE.equals(malicious)) {
                int vtScore = (score != null) ? Math.min(score.intValue(), 50) : 35;
                String categoryInfo = (categories != null && !categories.isEmpty())
                        ? String.join(", ", categories)
                        : "unknown";

                log.info("URLScan.io: Malicious verdict found. Categories: {}", categoryInfo);
                return Optional.of(ThreatIndicator.builder()
                        .type("URLSCAN_MALICIOUS")
                        .severity(Severity.HIGH)
                        .score(vtScore)
                        .message("URLScan.io reported this URL as malicious. Threat categories: " + categoryInfo)
                        .source(SOURCE)
                        .build());
            }

            log.info("URLScan.io: No malicious verdict for URL.");
            return Optional.empty();

        } catch (RestClientException e) {
            log.warn("URLScan.io API call failed. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during URLScan.io analysis. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
