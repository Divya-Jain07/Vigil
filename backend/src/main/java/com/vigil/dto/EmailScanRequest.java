package com.vigil.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailScanRequest {

    @NotBlank(message = "Sender email is required")
    private String sender;
    
    private String replyTo; // Optional

    private String subject; // Optional, though usually present

    @NotBlank(message = "Email body is required")
    private String body;
    
    private String rawHeaders; // Optional, for advanced checks like SPF/DKIM
}
