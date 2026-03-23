package com.reporting.portal.controller;


import com.reporting.portal.dto.CreateReportRequest;
import com.reporting.portal.dto.ReportDto;
import com.reporting.portal.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Fetches initial reports for the React Table
    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // Activated when 'Submit Report' Button is pressed
    @PostMapping
    public ResponseEntity<ReportDto> submitReport(@RequestBody CreateReportRequest request) {
        var savedReport = reportService.submitReport(request);
        return ResponseEntity.ok(savedReport);
    }
}
