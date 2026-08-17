package com.vigil.analyzer.email;

import com.vigil.dto.EmailScanRequest;
import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.ThreatLevel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailAuthenticationCheck implements EmailSecurityCheck {

    private static final Pattern AUTH_RESULTS_PATTERN = Pattern.compile("Authentication-Results:(.*?)(?=\\n\\S|\\Z)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern SPF_FAIL_PATTERN = Pattern.compile("spf=(?:fail|softfail|none)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DKIM_FAIL_PATTERN = Pattern.compile("dkim=(?:fail|none)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DMARC_FAIL_PATTERN = Pattern.compile("dmarc=(?:fail|none)", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<ThreatIndicator> analyze(EmailScanRequest request) {
        if (request.getRawHeaders() == null || request.getRawHeaders().isEmpty()) {
            return Optional.empty(); // Cannot check without headers
        }

        Matcher authMatcher = AUTH_RESULTS_PATTERN.matcher(request.getRawHeaders());
        if (authMatcher.find()) {
            String authResults = authMatcher.group(1);
            
            boolean spfFail = SPF_FAIL_PATTERN.matcher(authResults).find();
            boolean dkimFail = DKIM_FAIL_PATTERN.matcher(authResults).find();
            boolean dmarcFail = DMARC_FAIL_PATTERN.matcher(authResults).find();

            if (spfFail || dkimFail || dmarcFail) {
                StringBuilder reason = new StringBuilder("Email failed authentication checks: ");
                if (spfFail) reason.append("SPF failed/missing. ");
                if (dkimFail) reason.append("DKIM failed/missing. ");
                if (dmarcFail) reason.append("DMARC failed/missing.");
                
                return Optional.of(new ThreatIndicator(
                        "Email Authentication Failure",
                        reason.toString().trim(),
                        ThreatLevel.HIGH
                ));
            }
        } else {
             // Missing Authentication-Results header can also be suspicious, but might lead to false positives on internal emails.
             // We'll skip for now.
        }

        return Optional.empty();
    }
}
