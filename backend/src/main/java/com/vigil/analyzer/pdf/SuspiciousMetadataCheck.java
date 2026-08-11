package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SuspiciousMetadataCheck implements PdfSecurityCheck {

    private static final List<String> SUSPICIOUS_CREATORS = List.of(
            "msfvenom", "metasploit", "cobalt strike", "phishing", "malware"
    );

    @Override
    public Optional<ThreatIndicator> analyze(PdfParseResult result) {
        String creator = result.getMetadata().getOrDefault("Creator", "").toLowerCase();
        String producer = result.getMetadata().getOrDefault("Producer", "").toLowerCase();

        boolean suspiciousCreator = SUSPICIOUS_CREATORS.stream().anyMatch(creator::contains);
        boolean suspiciousProducer = SUSPICIOUS_CREATORS.stream().anyMatch(producer::contains);

        if (suspiciousCreator || suspiciousProducer) {
            return Optional.of(ThreatIndicator.builder()
                    .type("SUSPICIOUS_PDF_METADATA")
                    .severity(Severity.HIGH)
                    .score(60)
                    .message("The PDF was created or produced by a tool commonly associated with malware or exploitation (e.g., Metasploit, Msfvenom).")
                    .source("Local")
                    .build());
        }

        return Optional.empty();
    }
}
