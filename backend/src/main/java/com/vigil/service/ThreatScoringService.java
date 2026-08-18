package com.vigil.service;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreatScoringService {

    public ThreatScore calculateScore(List<ThreatIndicator> indicators) {
        if (indicators == null || indicators.isEmpty()) {
            return ThreatScore.builder()
                    .score(0)
                    .severity(Severity.LOW)
                    .build();
        }

        // Check for Critical Trigger Overrides
        boolean hasCriticalVt = indicators.stream()
                .anyMatch(i -> "VirusTotal".equalsIgnoreCase(i.getSource()) && (i.getSeverity() == Severity.HIGH || i.getSeverity() == Severity.CRITICAL));
                
        boolean hasNewDomain = indicators.stream().anyMatch(i -> "NEW_DOMAIN".equalsIgnoreCase(i.getType()));
        boolean hasHighEntropy = indicators.stream().anyMatch(i -> "HIGH_SUBDOMAIN_ENTROPY".equalsIgnoreCase(i.getType()));
        
        boolean criticalOverride = hasCriticalVt || (hasNewDomain && hasHighEntropy);

        int totalScore = indicators.stream()
                .mapToInt(ThreatIndicator::getScore)
                .sum();
                
        if (criticalOverride) {
            totalScore = Math.max(totalScore, 85); // Force floor to 85 (Critical)
        }
                
        int cappedScore = Math.min(totalScore, 100);

        return ThreatScore.builder()
                .score(cappedScore)
                .severity(determineSeverity(cappedScore))
                .build();
    }

    private Severity determineSeverity(int score) {
        if (score < 30) {
            return Severity.LOW;
        } else if (score < 60) {
            return Severity.MEDIUM;
        } else if (score < 80) {
            return Severity.HIGH;
        } else {
            return Severity.CRITICAL;
        }
    }
}
