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

        int totalScore = indicators.stream()
                .mapToInt(ThreatIndicator::getScore)
                .sum();
                
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
