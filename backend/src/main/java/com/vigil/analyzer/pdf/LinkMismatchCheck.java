package com.vigil.analyzer.pdf;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkMismatchCheck implements PdfSecurityCheck {

    private static final Pattern DOMAIN_IN_TEXT = Pattern.compile("([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");

    @Override
    public Optional<ThreatIndicator> analyze(PdfParseResult result) {
        if (result.getExtractedLinks() == null) return Optional.empty();

        for (PdfExtractedLink link : result.getExtractedLinks()) {
            String displayText = link.getDisplayText();
            if (displayText == null || displayText.isBlank()) continue;

            Matcher matcher = DOMAIN_IN_TEXT.matcher(displayText);
            if (!matcher.find()) continue;
            String displayedDomain = matcher.group().toLowerCase();

            try {
                String actualHost = URI.create(link.getActualUrl()).getHost();
                if (actualHost == null) continue;
                actualHost = actualHost.toLowerCase();

                if (!actualHost.contains(displayedDomain) && !displayedDomain.contains(actualHost)) {
                    return Optional.of(ThreatIndicator.builder()
                            .type("LINK_TEXT_MISMATCH")
                            .severity(Severity.HIGH).score(45)
                            .message("The document displays \"" + displayText
                                + "\" but the link actually points to " + link.getActualUrl()
                                + " — a common phishing technique.")
                            .source("Local").build());
                }
            } catch (Exception ignored) {}
        }
        return Optional.empty();
    }
}