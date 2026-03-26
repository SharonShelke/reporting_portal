package com.reporting.portal.service;

import com.reporting.portal.dto.CreateReportRequest;
import com.reporting.portal.dto.ReportDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.entity.User;
import com.reporting.portal.repository.ReportRepository;
import com.reporting.portal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReportDto submitReport(CreateReportRequest request) {
        var report = new Report();
        var totalCount = reportRepository.count();
        var newId = String.format("RPT-%03d", totalCount + 1);
        
        report.setId(newId);
        report.setRegion(request.region());
        report.setZone(request.zone());
        report.setCampaign(request.campaign());
        report.setAttendance(request.attendance());
        report.setNotes(request.notes());
        
        var submittedBy = request.submittedBy() != null && !request.submittedBy().isBlank() 
                          ? request.submittedBy() 
                          : "You";
        report.setSubmittedBy(submittedBy);
        
        return mapToDto(reportRepository.save(report));
    }

    public List<ReportDto> getAllReports(String email) {
        if (email == null || email.isBlank()) {
            return reportRepository.findAll().stream().map(this::mapToDto).toList();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || "admin".equals(user.getRole())) {
            return reportRepository.findAll().stream().map(this::mapToDto).toList();
        }

        if ("global".equals(user.getRole()) || "regional".equals(user.getRole())) {
            return reportRepository.findByRegion(user.getRegion())
                    .stream()
                    .map(this::mapToDto)
                    .toList();
        }

        String fullName = user.getFirstName() + (user.getLastName() != null && !user.getLastName().isBlank() ? " " + user.getLastName() : "");
        return reportRepository.findBySubmittedBy(fullName)
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
