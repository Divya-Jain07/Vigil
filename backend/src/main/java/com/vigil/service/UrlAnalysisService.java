package com.vigil.service;

import com.vigil.analyzer.url.UrlSecurityCheck;
import com.vigil.client.rdap.RdapClient;
import com.vigil.client.urlscan.UrlScanClient;
import com.vigil.client.virustotal.VirusTotalClient;
import com.vigil.model.ThreatIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full URL analysis pipeline.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlAnalysisService {

    private final List<UrlSecurityCheck> securityChecks;
    private final VirusTotalClient virusTotalClient;
    private final UrlScanClient urlScanClient;
    private final RdapClient rdapClient;

    private static final List<String> WHITELISTED_DOMAINS = List.of(
            "microsoft.com", "microsoftonline.com", "apple.com", "paypal.com",
            "amazon.com", "google.com", "github.com"
    );

    public List<ThreatIndicator> analyzeUrl(String url) {
        List<ThreatIndicator> indicators = new ArrayList<>();
        
        // Step 1: Whitelist Check
        if (isWhitelisted(url)) {
            log.info("URL {} is whitelisted. Skipping further checks.", url);
            return indicators; // Return empty, meaning 0 score/safe
        }

        // Phase 1: Local deterministic checks (Step 2 & 3)
        log.info("Running local security checks for URL: {}", url);
        securityChecks.stream()
                .map(check -> check.analyze(url))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(indicators::add);
        log.info("Local checks complete. {} indicator(s) found.", indicators.size());

        // Phase 2: External threat intelligence (each runs safely)
        log.info("Running external threat intelligence checks...");
        addIfPresent(indicators, virusTotalClient.analyze(url), "VirusTotal");
        addIfPresent(indicators, urlScanClient.analyze(url), "URLScan.io");
        addIfPresent(indicators, rdapClient.analyze(url), "RDAP");
        log.info("External checks complete. Total indicators: {}", indicators.size());

        return indicators;
    }
    
    private boolean isWhitelisted(String url) {
        if (url == null || url.isBlank()) return false;
        try {
            String urlToParse = url.startsWith("http") ? url : "http://" + url;
            URI uri = new URI(urlToParse);
            String host = uri.getHost();
            if (host == null) return false;
            
            // Use Guava's InternetDomainName to accurately extract the top private domain (eTLD+1)
            // This prevents subdomain hijacking (e.g., google.com.attacker.com)
            com.google.common.net.InternetDomainName domainName = com.google.common.net.InternetDomainName.from(host);
            if (!domainName.isUnderPublicSuffix()) return false;
            
            String topPrivate = domainName.topPrivateDomain().toString();
            return WHITELISTED_DOMAINS.contains(topPrivate);
        } catch (Exception e) {
            return false;
        }
    }

    private void addIfPresent(List<ThreatIndicator> list, Optional<ThreatIndicator> indicator, String source) {
        indicator.ifPresentOrElse(
                i -> { list.add(i); log.info("{} returned an indicator: {}", source, i.getType()); },
                () -> log.debug("{} returned no indicator.", source)
        );
    }
}
