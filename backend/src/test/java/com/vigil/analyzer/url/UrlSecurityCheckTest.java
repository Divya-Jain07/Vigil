package com.vigil.analyzer.url;

import com.vigil.model.ThreatIndicator;
import com.vigil.model.enums.Severity;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UrlSecurityCheckTest {

    @Test
    void testHttpsCheck() {
        HttpsCheck check = new HttpsCheck();
        
        Optional<ThreatIndicator> insecure = check.analyze("http://example.com");
        assertTrue(insecure.isPresent());
        assertEquals(Severity.LOW, insecure.get().getSeverity());
        assertEquals("INSECURE_PROTOCOL", insecure.get().getType());
        
        Optional<ThreatIndicator> secure = check.analyze("https://example.com");
        assertFalse(secure.isPresent());
    }

    @Test
    void testIpAddressInUrlCheck() {
        IpAddressInUrlCheck check = new IpAddressInUrlCheck();
        
        Optional<ThreatIndicator> ipUrl = check.analyze("http://192.168.1.1/login");
        assertTrue(ipUrl.isPresent());
        assertEquals(Severity.MEDIUM, ipUrl.get().getSeverity());
        assertEquals("IP_ADDRESS_URL", ipUrl.get().getType());
        
        Optional<ThreatIndicator> domainUrl = check.analyze("https://example.com/login");
        assertFalse(domainUrl.isPresent());
    }
    
    @Test
    void testSuspiciousTldCheck() {
        SuspiciousTldCheck check = new SuspiciousTldCheck();
        
        Optional<ThreatIndicator> suspicious = check.analyze("https://example.xyz");
        assertTrue(suspicious.isPresent());
        assertEquals(Severity.MEDIUM, suspicious.get().getSeverity());
        assertEquals("SUSPICIOUS_TLD", suspicious.get().getType());
        
        Optional<ThreatIndicator> safe = check.analyze("https://example.com");
        assertFalse(safe.isPresent());
    }
    
    @Test
    void testUrlLengthCheck() {
        UrlLengthCheck check = new UrlLengthCheck();
        
        String longUrl = "https://example.com/" + "a".repeat(150);
        Optional<ThreatIndicator> tooLong = check.analyze(longUrl);
        assertTrue(tooLong.isPresent());
        assertEquals(Severity.LOW, tooLong.get().getSeverity());
        assertEquals("EXCESSIVE_URL_LENGTH", tooLong.get().getType());
        
        Optional<ThreatIndicator> normalLength = check.analyze("https://example.com/path");
        assertFalse(normalLength.isPresent());
    }
}
