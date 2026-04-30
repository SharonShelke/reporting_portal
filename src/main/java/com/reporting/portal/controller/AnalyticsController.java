package com.reporting.portal.controller;

import com.reporting.portal.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/analytics", "/analytics"})
@CrossOrigin(origins = "http://65.0.71.13")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /api/analytics/stats?tab=overview&timeRange=This+Month&zone=All+Zones
     * Returns live KPI stats for the requested Analytics tab.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam(defaultValue = "overview")    String tab,
            @RequestParam(defaultValue = "This Month")  String timeRange,
            @RequestParam(defaultValue = "All Zones")   String zone) {
        return ResponseEntity.ok(analyticsService.getStats(tab, timeRange, zone));
    }
}
