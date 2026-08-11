package com.vigil.dto;

import com.vigil.model.ThreatIndicator;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UrlScanResponse {
    private boolean success;
    private ScanData data;

    @Data
    @Builder
    public static class ScanData {
        private String id;
        private String url;
        private int score;
        private String severity;
        private List<ThreatIndicator> indicators;
        private String status;
        private String explanation;
    }
}
