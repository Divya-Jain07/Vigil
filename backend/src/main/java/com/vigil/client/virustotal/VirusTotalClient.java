package com.vigil.client.virustotal;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client for the VirusTotal API v3.
 *
 * Submits a URL for analysis and retrieves the aggregated community verdict
 * (malicious, suspicious, harmless vote counts) from 70+ antivirus engines.
 *
 * Docs: https://developers.virustotal.com/reference/scan-url
 */
@Slf4j
@Component
public class VirusTotalClient {

    private static final String SOURCE = "VirusTotal";

    private final RestClient restClient;
    private final String apiKey;

    public VirusTotalClient(
            @Qualifier("virusTotalRestClient") RestClient restClient,
            @Value("${vigil.virustotal.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    /**
     * Analyzes a URL using VirusTotal.
     *
     * @param url The URL to analyze.
     * @return An Optional containing a ThreatIndicator if the URL has detections, or empty if clean or unavailable.
     */
    @SuppressWarnings("unchecked")
    public Optional<ThreatIndicator> analyze(String url) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("VirusTotal API key not configured. Skipping external check.");
            return Optional.empty();
        }

        try {
            // VirusTotal v3 uses a URL-safe base64 of the URL as the resource ID
            String urlId = Base64.getUrlEncoder().withoutPadding().encodeToString(url.getBytes());

            Map<String, Object> response = restClient.get()
                    .uri("/urls/{id}", urlId)
                    .header("x-apikey", apiKey)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                return Optional.empty();
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) return Optional.empty();

            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            if (attributes == null) return Optional.empty();

            Map<String, Object> lastAnalysisStats = (Map<String, Object>) attributes.get("last_analysis_stats");
            if (lastAnalysisStats == null) return Optional.empty();

            int malicious = toInt(lastAnalysisStats.get("malicious"));
            int suspicious = toInt(lastAnalysisStats.get("suspicious"));
            int totalDetections = malicious + suspicious;

            if (totalDetections == 0) {
                log.info("VirusTotal: No detections for URL.");
                return Optional.empty();
            }

            int score;
            Severity severity;
            String message;

            if (malicious >= 10) {
                score = 60;
                severity = Severity.CRITICAL;
                message = malicious + " antivirus engines flagged this URL as malicious.";
            } else if (malicious >= 3) {
                score = 40;
                severity = Severity.HIGH;
                message = malicious + " antivirus engines flagged this URL as malicious.";
            } else if (malicious >= 1 || suspicious >= 3) {
                score = 20;
                severity = Severity.MEDIUM;
                message = totalDetections + " engines reported this URL as suspicious or malicious.";
            } else {
                score = 10;
                severity = Severity.LOW;
                message = suspicious + " engines reported this URL as suspicious.";
            }

            log.info("VirusTotal: {} detections (malicious={}, suspicious={}) for URL.", totalDetections, malicious, suspicious);
            return Optional.of(ThreatIndicator.builder()
                    .type("VIRUSTOTAL_DETECTION")
                    .severity(severity)
                    .score(score)
                    .message(message)
                    .source(SOURCE)
                    .build());

        } catch (RestClientException e) {
            log.warn("VirusTotal API call failed. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during VirusTotal analysis. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Analyzes a file by its SHA-256 hash using VirusTotal.
     *
     * @param sha256 The SHA-256 hash of the file.
     * @param fileName The original file name (for logging/messages).
     * @return An Optional containing a ThreatIndicator if the file has detections.
     */
    @SuppressWarnings("unchecked")
    public Optional<ThreatIndicator> analyzeFileHash(String sha256, String fileName) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("VirusTotal API key not configured. Skipping file hash check.");
            return Optional.empty();
        }

        try {
            log.info("VirusTotal: Looking up file hash {} for '{}'", sha256, fileName);

            Map<String, Object> response = restClient.get()
                    .uri("/files/{id}", sha256)
                    .header("x-apikey", apiKey)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                return Optional.empty();
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) return Optional.empty();

            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            if (attributes == null) return Optional.empty();

            Map<String, Object> lastAnalysisStats = (Map<String, Object>) attributes.get("last_analysis_stats");
            if (lastAnalysisStats == null) return Optional.empty();

            int malicious = toInt(lastAnalysisStats.get("malicious"));
            int suspicious = toInt(lastAnalysisStats.get("suspicious"));
            int totalDetections = malicious + suspicious;

            if (totalDetections == 0) {
                log.info("VirusTotal: No detections for file hash.");
                return Optional.empty();
            }

            int score;
            Severity severity;
            String message;

            if (malicious >= 10) {
                score = 70;
                severity = Severity.CRITICAL;
                message = malicious + " antivirus engines flagged this file as malicious.";
            } else if (malicious >= 3) {
                score = 50;
                severity = Severity.HIGH;
                message = malicious + " antivirus engines flagged this file as malicious.";
            } else if (malicious >= 1 || suspicious >= 3) {
                score = 30;
                severity = Severity.MEDIUM;
                message = totalDetections + " engines reported this file as suspicious or malicious.";
            } else {
                score = 15;
                severity = Severity.LOW;
                message = suspicious + " engines reported this file as suspicious.";
            }

            log.info("VirusTotal: {} file detections (malicious={}, suspicious={}).", totalDetections, malicious, suspicious);
            return Optional.of(ThreatIndicator.builder()
                    .type("VIRUSTOTAL_FILE_DETECTION")
                    .severity(severity)
                    .score(score)
                    .message(message)
                    .source(SOURCE)
                    .build());

        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                log.info("VirusTotal: File hash not found in database (unknown file, assuming clean).");
            } else {
                log.warn("VirusTotal file hash lookup failed. Skipping. Reason: {}", e.getMessage());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during VirusTotal file hash analysis. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private int toInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        return 0;
    }
}
