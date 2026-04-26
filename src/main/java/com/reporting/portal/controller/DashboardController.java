package com.reporting.portal.controller;

import com.reporting.portal.dto.DashboardStatsDto;
import com.reporting.portal.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://65.0.71.13")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats(@org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        return ResponseEntity.ok(dashboardService.getDashboardStats(email));
    }
}
