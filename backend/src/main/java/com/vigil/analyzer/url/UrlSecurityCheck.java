package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import java.util.Optional;

public interface UrlSecurityCheck {
    Optional<ThreatIndicator> analyze(String url);
}
