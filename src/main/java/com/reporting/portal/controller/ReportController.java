package com.reporting.portal.controller;


import com.reporting.portal.dto.CreateReportRequest;
import com.reporting.portal.dto.ReportDto;
import com.reporting.portal.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<ReportDto>> getAllReports(@RequestParam(required = false) String email) {
        return ResponseEntity.ok(reportService.getAllReports(email));
    }

    // Activated when 'Submit Report' Button is pressed
    @PostMapping
    public ResponseEntity<ReportDto> submitReport(@RequestBody CreateReportRequest request) {
        var savedReport = reportService.submitReport(request);
        return ResponseEntity.ok(savedReport);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReports(@RequestParam(required = false) String email) {
        byte[] excelData = reportService.exportReportsToExcel(email);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weekly-reports.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] templateData = reportService.getTemplateFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=weekly-report-template.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(templateData);
    }
    @GetMapping("/template/{type}")
    public ResponseEntity<byte[]> downloadTemplateByType(@PathVariable String type) {
        byte[] data;
        HttpHeaders headers = new HttpHeaders();
        switch (type.toLowerCase()) {
            case "csv":
                data = reportService.getTemplateFile();
                headers.setContentDispositionFormData("attachment", "weekly-report-template.csv");
                headers.setContentType(MediaType.TEXT_PLAIN);
                break;
            case "xlsx":
                data = reportService.getEmptyXlsxTemplate();
                headers.setContentDispositionFormData("attachment", "weekly-report-template.xlsx");
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                break;
            case "doc":
                data = reportService.getTemplateFile();
                headers.setContentDispositionFormData("attachment", "weekly-report-template.doc");
                headers.setContentType(MediaType.parseMediaType("application/msword"));
                break;
            case "docx":
                data = reportService.getTemplateFile();
                headers.setContentDispositionFormData("attachment", "weekly-report-template.docx");
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
                break;
            case "pdf":
                // placeholder: reuse CSV as PDF content
                data = reportService.getTemplateFile();
                headers.setContentDispositionFormData("attachment", "weekly-report-template.pdf");
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<ReportDto>> uploadReports(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(reportService.uploadReports(file));
    }
}
