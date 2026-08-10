package com.vigil.service;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import com.vigil.model.enums.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreatScoringServiceTest {

    private ThreatScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ThreatScoringService();
    }

    @Test
    void testEmptyIndicators() {
        ThreatScore score = scoringService.calculateScore(new ArrayList<>());
        assertEquals(0, score.getScore());
        assertEquals(Severity.LOW, score.getSeverity());
    }

    @Test
    void testLowSeverityScore() {
        List<ThreatIndicator> indicators = List.of(
                ThreatIndicator.builder().score(10).build(),
                ThreatIndicator.builder().score(15).build()
        );
        ThreatScore score = scoringService.calculateScore(indicators);
        assertEquals(25, score.getScore());
        assertEquals(Severity.LOW, score.getSeverity());
    }

    @Test
    void testMediumSeverityScore() {
        List<ThreatIndicator> indicators = List.of(
                ThreatIndicator.builder().score(20).build(),
                ThreatIndicator.builder().score(25).build()
        );
        ThreatScore score = scoringService.calculateScore(indicators);
        assertEquals(45, score.getScore());
        assertEquals(Severity.MEDIUM, score.getSeverity());
    }

    @Test
    void testHighSeverityScore() {
        List<ThreatIndicator> indicators = List.of(
                ThreatIndicator.builder().score(40).build(),
                ThreatIndicator.builder().score(30).build()
        );
        ThreatScore score = scoringService.calculateScore(indicators);
        assertEquals(70, score.getScore());
        assertEquals(Severity.HIGH, score.getSeverity());
    }

    @Test
    void testCriticalSeverityScore() {
        List<ThreatIndicator> indicators = List.of(
                ThreatIndicator.builder().score(50).build(),
                ThreatIndicator.builder().score(40).build()
        );
        ThreatScore score = scoringService.calculateScore(indicators);
        assertEquals(90, score.getScore());
        assertEquals(Severity.CRITICAL, score.getSeverity());
    }

    @Test
    void testScoreCappedAt100() {
        List<ThreatIndicator> indicators = List.of(
                ThreatIndicator.builder().score(60).build(),
                ThreatIndicator.builder().score(50).build()
        );
        ThreatScore score = scoringService.calculateScore(indicators);
        assertEquals(100, score.getScore());
        assertEquals(Severity.CRITICAL, score.getSeverity());
    }
}
