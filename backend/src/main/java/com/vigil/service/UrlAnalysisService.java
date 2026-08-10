package com.vigil.service;

import com.vigil.analyzer.url.UrlSecurityCheck;
import com.vigil.model.ThreatIndicator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlAnalysisService {

    private final List<UrlSecurityCheck> securityChecks;

    public List<ThreatIndicator> analyzeUrl(String url) {
        return securityChecks.stream()
                .map(check -> check.analyze(url))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
