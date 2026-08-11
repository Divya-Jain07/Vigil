package com.vigil.client.rdap;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client for RDAP (Registration Data Access Protocol) domain lookups.
 *
 * Uses rdap.org as a public RDAP bootstrap server to find domain
 * registration information. Flags domains that are newly registered
 * (less than 30 days old), which is a strong phishing indicator.
 *
 * Docs: https://www.rdap.org/
 */
@Slf4j
@Component
public class RdapClient {

    private static final int NEW_DOMAIN_THRESHOLD_DAYS = 30;
    private static final String SOURCE = "RDAP";

    private final RestClient restClient;

    public RdapClient(@Qualifier("rdapRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Looks up domain registration date via RDAP and flags newly registered domains.
     *
     * @param url The URL to check.
     * @return An Optional ThreatIndicator if the domain is newly registered, or empty otherwise.
     */
    @SuppressWarnings("unchecked")
    public Optional<ThreatIndicator> analyze(String url) {
        try {
            String domain = extractDomain(url);
            if (domain == null) {
                log.debug("RDAP: Could not extract domain from URL: {}", url);
                return Optional.empty();
            }

            Map<String, Object> response = restClient.get()
                    .uri("/domain/{domain}", domain)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return Optional.empty();

            // RDAP events contain registration date under "registration" event
            List<Map<String, Object>> events = (List<Map<String, Object>>) response.get("events");
            if (events == null) return Optional.empty();

            for (Map<String, Object> event : events) {
                String action = (String) event.get("eventAction");
                if ("registration".equalsIgnoreCase(action)) {
                    String eventDate = (String) event.get("eventDate");
                    return evaluateRegistrationDate(domain, eventDate);
                }
            }

            log.debug("RDAP: No registration event found for domain: {}", domain);
            return Optional.empty();

        } catch (RestClientException e) {
            log.warn("RDAP lookup failed. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during RDAP analysis. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<ThreatIndicator> evaluateRegistrationDate(String domain, String eventDate) {
        if (eventDate == null || eventDate.isBlank()) return Optional.empty();

        try {
            // RDAP dates are in ISO 8601 format: 2024-01-15T12:00:00Z
            LocalDate registrationDate = LocalDate.parse(
                    eventDate.substring(0, 10),
                    DateTimeFormatter.ISO_LOCAL_DATE
            );

            long daysSinceRegistration = ChronoUnit.DAYS.between(registrationDate, LocalDate.now());

            if (daysSinceRegistration < NEW_DOMAIN_THRESHOLD_DAYS) {
                log.info("RDAP: Newly registered domain detected. Domain: {}, Age: {} days", domain, daysSinceRegistration);
                return Optional.of(ThreatIndicator.builder()
                        .type("NEWLY_REGISTERED_DOMAIN")
                        .severity(Severity.HIGH)
                        .score(35)
                        .message("Domain '" + domain + "' was registered only " + daysSinceRegistration
                                + " day(s) ago. Newly registered domains are a strong indicator of phishing.")
                        .source(SOURCE)
                        .build());
            }

            log.debug("RDAP: Domain {} is {} days old — not flagged.", domain, daysSinceRegistration);
            return Optional.empty();

        } catch (DateTimeParseException e) {
            log.warn("RDAP: Could not parse registration date '{}'. Skipping.", eventDate);
            return Optional.empty();
        }
    }

    /**
     * Extracts the registrable domain from a URL string.
     * E.g., "https://sub.example.com/path" -> "example.com"
     */
    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) return null;

            // Strip leading "www."
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return null;
        }
    }
}
