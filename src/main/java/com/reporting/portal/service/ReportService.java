package com.reporting.portal.service;


import com.reporting.portal.dto.CreateReportRequest;
import com.reporting.portal.dto.ReportDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public ReportDto submitReport(CreateReportRequest request) {
        var report = new Report();
        
        // Dynamically calculate the next RPT- ID (e.g. RPT-006) based on existing rows
        var totalCount = reportRepository.count();
        var newId = String.format("RPT-%03d", totalCount + 1);
        
        report.setId(newId);
        report.setRegion(request.region());
        report.setZone(request.zone());
        report.setCampaign(request.campaign());
        report.setAttendance(request.attendance());
        report.setNotes(request.notes());
        
        // Ensure there is a submittedBy name, falling back to 'You' to prevent null
        var submittedBy = request.submittedBy() != null && !request.submittedBy().isBlank() 
                          ? request.submittedBy() 
                          : "You";
        report.setSubmittedBy(submittedBy);
        
        var savedReport = reportRepository.save(report);
        return mapToDto(savedReport);
    }

    public List<ReportDto> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ReportDto mapToDto(Report report) {
        return new ReportDto(
            report.getId(),
            report.getRawDate() != null ? report.getRawDate().toString() : LocalDate.now().toString(),
            report.getRegion(),
            report.getZone(),
            report.getCampaign(),
            report.getSubmittedBy(),
            report.getAttendance(),
            report.getNotes(),
            report.getStatus()
        );
    }
}
