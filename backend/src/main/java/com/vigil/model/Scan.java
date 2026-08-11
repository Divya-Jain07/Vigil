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
    
    private String url;
    
    private ThreatScore threatScore;
    
    private List<ThreatIndicator> indicators;

    private String explanation;

    private String status;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
