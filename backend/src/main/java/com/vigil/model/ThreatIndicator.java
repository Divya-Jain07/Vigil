package com.vigil.model;

import com.vigil.model.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreatIndicator {
    private String type;
    private Severity severity;
    private int score;
    private String message;
    private String source;
}
