package com.reporting.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/")
    public ResponseEntity<java.util.Map<String, String>> root() {
        return ResponseEntity.ok(java.util.Map.of("status", "UP", "message", "Reporting Portal API is running"));
    }
    
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        return ResponseEntity.ok(java.util.Map.of("status", "UP", "message", "Backend is alive"));
    }
}
