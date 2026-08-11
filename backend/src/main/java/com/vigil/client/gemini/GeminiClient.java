package com.vigil.client.gemini;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client for the Google Gemini API (REST).
 *
 * Sends a text prompt to the Gemini generateContent endpoint and
 * returns the generated text response.
 *
 * Docs: https://ai.google.dev/api/generate-content
 */
@Slf4j
@Component
public class GeminiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Qualifier("geminiRestClient") RestClient restClient,
            @Value("${vigil.gemini.api-key}") String apiKey,
            @Value("${vigil.gemini.model}") String model) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    /**
     * Sends a prompt to Gemini and returns the generated text.
     *
     * @param prompt The text prompt to send.
     * @return Optional containing the response text, or empty if unavailable.
     */
    @SuppressWarnings("unchecked")
    public Optional<String> generate(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key not configured. Skipping AI explanation.");
            return Optional.empty();
        }

        try {
            // Build the request body in Gemini's required format
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            Map<String, Object> response = restClient.post()
                    .uri("/models/{model}:generateContent?key={key}", model, apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return Optional.empty();

            // Navigate: response -> candidates[0] -> content -> parts[0] -> text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return Optional.empty();

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) return Optional.empty();

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) return Optional.empty();

            String text = (String) parts.get(0).get("text");
            if (text == null || text.isBlank()) return Optional.empty();

            return Optional.of(text.trim());

        } catch (RestClientException e) {
            log.warn("Gemini API call failed. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Unexpected error during Gemini API call. Skipping. Reason: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
