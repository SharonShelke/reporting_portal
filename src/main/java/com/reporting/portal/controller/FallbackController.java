package com.reporting.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    
    @RequestMapping("/**")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.ok("Backend is alive");
    }
}
