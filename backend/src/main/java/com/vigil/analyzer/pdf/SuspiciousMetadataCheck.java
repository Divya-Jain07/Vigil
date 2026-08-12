package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SuspiciousMetadataCheck implements PdfSecurityCheck {

    private static final List<String> SUSPICIOUS_METADATA_KEYWORDS = List.of(
        "urgent", "password", "verify", "account", "reset required", "final", "admin"
    );

    private static final List<String> SUSPICIOUS_CREATORS = List.of(
        "metasploit", "kail", "kali", "exploit", "cobalt strike", "nmap"
    );
@Override
public Optional<ThreatIndicator> analyze(PdfParseResult result) {
    String creator = result.getMetadata().getOrDefault("Creator", "").toLowerCase();
    String producer = result.getMetadata().getOrDefault("Producer", "").toLowerCase();
    String title = result.getMetadata().getOrDefault("Title", "").toLowerCase();
    String subject = result.getMetadata().getOrDefault("Subject", "").toLowerCase();
    String author = result.getMetadata().getOrDefault("Author", "").toLowerCase();

    boolean suspiciousTool = SUSPICIOUS_CREATORS.stream()
            .anyMatch(k -> creator.contains(k) || producer.contains(k));
    if (suspiciousTool) {
        return Optional.of(ThreatIndicator.builder()
                .type("SUSPICIOUS_PDF_METADATA")
                .severity(Severity.HIGH).score(60)
                .message("The PDF was created or produced by a tool commonly associated with malware or exploitation.")
                .source("Local").build());
    }

    boolean suspiciousContent = SUSPICIOUS_METADATA_KEYWORDS.stream()
            .anyMatch(k -> title.contains(k) || subject.contains(k) || author.contains(k));
    if (suspiciousContent) {
        return Optional.of(ThreatIndicator.builder()
                .type("SUSPICIOUS_METADATA_CONTENT")
                .severity(Severity.MEDIUM).score(20)
                .message("The document's title, subject, or author metadata contains terms unusual for legitimate documents.")
                .source("Local").build());
    }
    return Optional.empty();
}
}
