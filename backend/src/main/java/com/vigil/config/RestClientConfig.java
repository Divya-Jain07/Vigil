package com.vigil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${vigil.virustotal.base-url}")
    private String virusTotalBaseUrl;

    @Value("${vigil.urlscan.base-url}")
    private String urlScanBaseUrl;

    @Value("${vigil.rdap.base-url}")
    private String rdapBaseUrl;

    @Value("${vigil.gemini.base-url}")
    private String geminiBaseUrl;

    @Bean("virusTotalRestClient")
    public RestClient virusTotalRestClient() {
        return RestClient.builder()
                .baseUrl(virusTotalBaseUrl)
                .build();
    }

    @Bean("urlScanRestClient")
    public RestClient urlScanRestClient() {
        return RestClient.builder()
                .baseUrl(urlScanBaseUrl)
                .build();
    }

    @Bean("rdapRestClient")
    public RestClient rdapRestClient() {
        return RestClient.builder()
                .baseUrl(rdapBaseUrl)
                .build();
    }

    @Bean("geminiRestClient")
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(geminiBaseUrl)
                .build();
    }
}
