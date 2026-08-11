package com.vigil.service;

import com.vigil.analyzer.url.UrlSecurityCheck;
import com.vigil.client.rdap.RdapClient;
import com.vigil.client.urlscan.UrlScanClient;
import com.vigil.client.virustotal.VirusTotalClient;
import com.vigil.model.ThreatIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full URL analysis pipeline:
 * 1. Local deterministic security checks (HTTPS, TLD, URL length, IP, etc.)
 * 2. External threat intelligence (VirusTotal, URLScan.io, RDAP)
 *
 * External failures are gracefully handled — local results are always returned
 * even if external services are unavailable.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UrlAnalysisService {

    private final List<UrlSecurityCheck> securityChecks;
    private final VirusTotalClient virusTotalClient;
    private final UrlScanClient urlScanClient;
    private final RdapClient rdapClient;

    public List<ThreatIndicator> analyzeUrl(String url) {
        List<ThreatIndicator> indicators = new ArrayList<>();

        // Phase 1: Local deterministic checks
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

    private void addIfPresent(List<ThreatIndicator> list, Optional<ThreatIndicator> indicator, String source) {
        indicator.ifPresentOrElse(
                i -> { list.add(i); log.info("{} returned an indicator: {}", source, i.getType()); },
                () -> log.debug("{} returned no indicator.", source)
        );
    }
}
