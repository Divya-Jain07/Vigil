package com.vigil.controller;

import com.vigil.model.Scan;
import com.vigil.repository.ScanRepository;
import com.vigil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/scans")
@RequiredArgsConstructor
public class ScanHistoryController {

    private final ScanRepository scanRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Scan>> getUserScans() {
        String userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<Scan> scans = scanRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(scans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scan> getScanById(@PathVariable String id) {
        String userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Scan> scanOptional = scanRepository.findById(id);
        
        if (scanOptional.isPresent()) {
            Scan scan = scanOptional.get();
            // Ensure the authenticated user actually owns this scan
            if (userId.equals(scan.getUserId())) {
                return ResponseEntity.ok(scan);
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    private String getAuthenticatedUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                return userRepository.findByEmail(email).map(com.vigil.model.User::getId).orElse(null);
            }
        }
        return null;
    }
}
