package com.vigil.service;

import com.vigil.client.gemini.GeminiClient;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.ThreatScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that uses Gemini to generate a human-readable explanation of
 * the security analysis results.
 *
 * Gemini's role is purely explanatory. It does NOT influence the threat score
 * or determine whether a URL is malicious. The deterministic analysis and
 * external threat intelligence results are the source of truth.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiExplanationService {

    private final GeminiClient geminiClient;

    /**
     * Generates a human-readable security explanation for the given analysis results.
     *
     * @param url        The URL that was analyzed.
     * @param score      The calculated threat score.
     * @param indicators The list of threat indicators found.
     * @return A plain-text explanation from Gemini, or a default message if unavailable.
     */
    public String explain(String url, ThreatScore score, List<ThreatIndicator> indicators) {
        String prompt = buildPrompt(url, score, indicators);

        return geminiClient.generate(prompt)
                .orElse(buildFallbackExplanation(score, indicators));
    }

    public String explainEmail(String subject, ThreatScore score, List<ThreatIndicator> indicators) {
        String prompt = buildEmailPrompt(subject, score, indicators);
        return geminiClient.generate(prompt)
                .orElse(buildFallbackExplanation(score, indicators, "email"));
    }

    public String explainPdf(String fileName, ThreatScore score, List<ThreatIndicator> indicators) {
        String prompt = buildPdfPrompt(fileName, score, indicators);
        return geminiClient.generate(prompt)
                .orElse(buildFallbackExplanation(score, indicators, "PDF"));
    }

    private String buildPrompt(String url, ThreatScore score, List<ThreatIndicator> indicators) {
        return buildBasePrompt("URL", url, score, indicators);
    }
    
    private String buildEmailPrompt(String subject, ThreatScore score, List<ThreatIndicator> indicators) {
        return buildBasePrompt("Email", "Subject: " + (subject != null ? subject : "(No subject)"), score, indicators);
    }

    private String buildPdfPrompt(String fileName, ThreatScore score, List<ThreatIndicator> indicators) {
        return buildBasePrompt("PDF document", "File: " + (fileName != null ? fileName : "(Unknown)"), score, indicators);
    }

    private String buildBasePrompt(String type, String target, ThreatScore score, List<ThreatIndicator> indicators) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a security analyst assistant for the Vigil security platform. ");
        sb.append("A user has submitted an ").append(type).append(" for security analysis. ");
        sb.append("Your job is to explain the findings in clear, plain English that a non-technical user can understand.\n\n");

        sb.append(type).append(" analyzed: ").append(target).append("\n");
        sb.append("Risk Score: ").append(score.getScore()).append("/100\n");
        sb.append("Severity: ").append(score.getSeverity().name()).append("\n\n");

        if (indicators.isEmpty()) {
            sb.append("No specific threat indicators were detected.\n\n");
        } else {
            sb.append("Security indicators found (").append(indicators.size()).append("):\n");
            for (ThreatIndicator indicator : indicators) {
                sb.append("- [").append(indicator.getSeverity().name()).append("] ")
                  .append(indicator.getType()).append(" (from ").append(indicator.getSource()).append("): ")
                  .append(indicator.getMessage()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("Please provide a concise response in exactly this format:\n");
        sb.append("SUMMARY: (1-2 sentences summarizing whether this ").append(type).append(" appears safe or dangerous)\n");
        sb.append("WHY: (brief explanation of the key reasons, referencing specific indicators if present)\n");
        sb.append("ACTION: (a clear, specific recommended action for the user — e.g., avoid this, proceed with caution, or it appears safe)\n\n");
        sb.append("Keep the total response under 150 words. Do not use markdown formatting.");

        return sb.toString();
    }

    /**
     * Provides a rule-based fallback explanation when Gemini is unavailable.
     */
    private String buildFallbackExplanation(ThreatScore score, List<ThreatIndicator> indicators) {
        return buildFallbackExplanation(score, indicators, "URL");
    }

    private String buildFallbackExplanation(ThreatScore score, List<ThreatIndicator> indicators, String type) {
        if (indicators.isEmpty()) {
            return "SUMMARY: No threats were detected for this " + type + ".\n" +
                   "WHY: All security checks passed without any suspicious findings.\n" +
                   "ACTION: This " + type + " appears safe, but always exercise caution online.";
        }

        String topIndicator = indicators.stream()
                .max((a, b) -> Integer.compare(a.getScore(), b.getScore()))
                .map(ThreatIndicator::getMessage)
                .orElse("Multiple suspicious patterns detected.");

        String action = switch (score.getSeverity()) {
            case CRITICAL -> "Do not trust this " + type + " under any circumstances.";
            case HIGH     -> "Avoid this " + type + ". It shows strong indicators of malicious activity.";
            case MEDIUM   -> "Exercise caution. Do not click links or enter personal information.";
            case LOW      -> "This " + type + " has minor suspicious characteristics. Proceed with caution.";
        };

        return "SUMMARY: This " + type + " has a " + score.getSeverity().name() + " risk level with a score of " + score.getScore() + "/100.\n" +
               "WHY: " + topIndicator + "\n" +
               "ACTION: " + action;
    }
}
