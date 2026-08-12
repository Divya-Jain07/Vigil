package com.vigil.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "scans")
public class Scan {
    @Id
    private String id;
    
    private String userId;
    
    // e.g., "URL", "EMAIL", "PDF"
    private String inputType;
    
    // For URL scans, or URLs extracted from emails
    private String url;
    
    // For Email scans
    private String emailSubject;
    private String emailSender;
    
    // For PDF scans
    private String fileName;
    
    private ThreatScore threatScore;
    
    private List<ThreatIndicator> indicators;

    private String explanation;

    private String status;
    
    public String getInputType() {
        if (inputType != null) {
            return inputType;
        }
        if (fileName != null) {
            return "PDF";
        }
        if (emailSubject != null || emailSender != null) {
            return "EMAIL";
        }
        if (url != null) {
            return "URL";
        }
        return "UNKNOWN";
    }
    
    @CreatedDate
    private LocalDateTime createdAt;
}
