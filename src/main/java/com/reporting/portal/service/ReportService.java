package com.reporting.portal.service;

import com.reporting.portal.AttendanceStatus;
import com.reporting.portal.dto.CreateReportRequest;
import com.reporting.portal.dto.ReportDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.entity.User;
import com.reporting.portal.repository.ReportRepository;
import com.reporting.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // ===================== SUBMIT =====================
    @Transactional
    public ReportDto submitReport(CreateReportRequest request) {
        Report report = new Report();

        report.setSubmittedBy(request.submittedBy());
        report.setSubmitterEmail(request.submitterEmail());
        report.setSubmittedDate(request.submittedDate());
        report.setSubmittedTime(request.submittedTime());
        report.setWeekStartDate(request.weekStartDate());

        report.setZoneName(request.zoneName());
        report.setZonalManager(request.zonalManager());

        report.setTotalPartnershipRemittance(request.totalPartnershipRemittance());
        report.setNewPartnersRecruited(request.newPartnersRecruited());
        report.setTestimoniesSubmitted(request.testimoniesSubmitted());
        report.setHttnmTranslations(request.httnmTranslations());
        report.setHttnmOutreachesHeld(request.httnmOutreachesHeld());
        report.setHttnmMediaSubmitted(request.httnmMediaSubmitted());

        report.setZonalPastorDirectorsMeeting(
                AttendanceStatus.valueOf(formatEnum(request.zonalPastorDirectorsMeeting()))
        );
        report.setZonalManagerDirectorsMeeting(
                AttendanceStatus.valueOf(formatEnum(request.zonalManagerDirectorsMeeting()))
        );
        report.setZonalManagerStrategyMeeting(
                AttendanceStatus.valueOf(formatEnum(request.zonalManagerStrategyMeeting()))
        );

        report.setHealingCrusadeSponsorship(
                request.healingCrusadeSponsorship() != null
                        ? request.healingCrusadeSponsorship()
                        : BigDecimal.ZERO
        );
        report.setTestimonyClarificationConcern(request.testimonyClarificationConcern());
        report.setRegionName(request.regionName());
        
        report.setRemittancePurpose(request.remittancePurpose());
        report.setTrumpetsBlown(request.trumpetsBlown() != null ? request.trumpetsBlown() : 0);
        report.setPopMediaUrl(request.popMediaUrl());
        
        report.setStatus("PENDING");

        Report saved = reportRepository.save(report);

        // Notify Sharon for approval
        try {
            emailService.sendSimpleEmail("sharonshelke7@gmail.com", "New Report for Approval",
                "A new report has been submitted by " + report.getSubmittedBy() + " (" + report.getZoneName() + "). Please log in to approve it.");
        } catch (Exception e) {
            System.err.println("Approval email notification failed: " + e.getMessage());
        }

        return mapToDto(saved);
    }

    // ===================== FETCH =====================
    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports(String email) {
        if (email == null || email.isBlank()) {
            return mapAll(reportRepository.findAll());
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return List.of();

        return switch (user.getRole()) {
            case "admin"  -> mapAll(reportRepository.findAll());
            case "global" -> mapAll(reportRepository.findByRegionName(user.getRegion()));
            default -> mapAll(reportRepository.findBySubmitterEmail(user.getEmail()));
        };
    }

    // ===================== EXPORT =====================
    @Transactional(readOnly = true)
    public byte[] exportReportsToExcel(String email) {
        List<ReportDto> reports = getAllReports(email);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Weekly Report");

            String[] headers = {
                    "ID",
                    "Submitted At",
                    "Zone Name",
                    "Zonal Manager",
                    "Total Partnership Remittance",
                    "New Partners",
                    "Testimonies",
                    "HTTNM Translations",
                    "HTTNM Outreaches",
                    "Media Submitted",
                    "Pastor Meeting",
                    "Director Meeting",
                    "Strategy Meeting",
                    "Crusade Sponsorship",
                    "Notes",
                    "Submitted Email",
                    "Remittance Purpose",
                    "Trumpets Blown",
                    "POP Media URL"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;
            for (ReportDto r : reports) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(r.id() != null ? r.id() : 0);
                row.createCell(1).setCellValue(orEmpty(r.createdAt()));
                row.createCell(2).setCellValue(orEmpty(r.zoneName()));
                row.createCell(3).setCellValue(orEmpty(r.zonalManager()));
                row.createCell(4).setCellValue(toDoubleSafe(r.totalPartnershipRemittance()));
                row.createCell(5).setCellValue(toIntSafe(r.newPartnersRecruited()));
                row.createCell(6).setCellValue(toIntSafe(r.testimoniesSubmitted()));
                row.createCell(7).setCellValue(toIntSafe(r.httnmTranslations()));
                row.createCell(8).setCellValue(toIntSafe(r.httnmOutreaches()));
                row.createCell(9).setCellValue(toIntSafe(r.outreachMediaSubmitted()));
                row.createCell(10).setCellValue(orEmpty(r.zonalPastorAttendance()));
                row.createCell(11).setCellValue(orEmpty(r.zonalManagerDirectorMeetingAttendance()));
                row.createCell(12).setCellValue(orEmpty(r.zonalManagerStrategyMeetingAttendance()));
                row.createCell(13).setCellValue(toDoubleSafe(r.sponsorshipHealingCrusade()));
                row.createCell(14).setCellValue(orEmpty(r.testimonyClarificationConcern()));
                row.createCell(15).setCellValue(orEmpty(r.submittedByEmail()));
                row.createCell(16).setCellValue(orEmpty(r.remittancePurpose()));
                row.createCell(17).setCellValue(toIntSafe(r.trumpetsBlown()));
                row.createCell(18).setCellValue(orEmpty(r.popMediaUrl()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Unable to export reports", e);
        }
    }

    // ===================== TEMPLATE =====================
    public byte[] getTemplateFile() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/weekly-report-template.csv");
            return resource.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load template", e);
        }
    }

    public byte[] getEmptyXlsxTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Weekly Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Submitted By");
            header.createCell(1).setCellValue("Email");
            header.createCell(2).setCellValue("Date (yyyy-MM-dd)");
            header.createCell(3).setCellValue("Time (HH:mm:ss)");
            header.createCell(4).setCellValue("Week Start Date");

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Template error", e);
        }
    }

    // ===================== UPLOAD =====================
    @Transactional
    public List<ReportDto> uploadReports(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Upload file required");
        }

        try {
            String name = file.getOriginalFilename().toLowerCase();

            if (name.endsWith(".csv")) return uploadCsv(file);
            if (name.endsWith(".xlsx") || name.endsWith(".xls")) return uploadExcel(file);

            throw new RuntimeException("Unsupported file type");

        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    private List<ReportDto> uploadCsv(MultipartFile file) throws IOException {
        List<ReportDto> saved = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                String[] c = line.split(",", -1);

                CreateReportRequest req = new CreateReportRequest(
                        c[0],
                        c[1],
                        LocalDate.parse(c[2]),
                        LocalTime.parse(c[3]),
                        LocalDate.parse(c[4]),
                        c[5],
                        c[6],
                        new BigDecimal(c[7]),
                        toInt(c[8]),
                        toInt(c[9]),
                        toInt(c[10]),
                        toInt(c[11]),
                        toInt(c[12]),
                        c[13],
                        c[14],
                        c[15],
                        new BigDecimal(c[16]),
                        c[17],
                        c[18],
                        c.length > 19 ? c[19] : null,
                        c.length > 20 ? toInt(c[20]) : null,
                        c.length > 21 ? c[21] : null
                );

                saved.add(submitReport(req));
            }
        }
        return saved;
    }

    private List<ReportDto> uploadExcel(MultipartFile file) throws IOException {
        List<ReportDto> saved = new ArrayList<>();

        try (var workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                CreateReportRequest req = new CreateReportRequest(
                        cell(row, 0),
                        cell(row, 1),
                        LocalDate.parse(cell(row, 2)),
                        LocalTime.parse(cell(row, 3)),
                        LocalDate.parse(cell(row, 4)),
                        cell(row, 5),
                        cell(row, 6),
                        new BigDecimal(cell(row, 7)),
                        toInt(cell(row, 8)),
                        toInt(cell(row, 9)),
                        toInt(cell(row, 10)),
                        toInt(cell(row, 11)),
                        toInt(cell(row, 12)),
                        cell(row, 13),
                        cell(row, 14),
                        cell(row, 15),
                        new BigDecimal(cell(row, 16)),
                        cell(row, 17),
                        cell(row, 18),
                        cell(row, 19),
                        toInt(cell(row, 20)),
                        cell(row, 21)
                );

                saved.add(submitReport(req));
            }
        }
        return saved;
    }

    // ===================== MAPPERS =====================
    private List<ReportDto> mapAll(List<Report> reports) {
        return reports.stream().map(this::mapToDto).toList();
    }

    private ReportDto mapToDto(Report r) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new ReportDto(
                r.getId(),
                r.getSubmittedAt() != null ? r.getSubmittedAt().format(f) : "",
                r.getZoneName(),
                r.getZonalManager(),
                r.getTotalPartnershipRemittance(),
                r.getNewPartnersRecruited(),
                r.getTestimoniesSubmitted(),
                r.getHttnmTranslations(),
                r.getHttnmOutreachesHeld(),
                r.getHttnmMediaSubmitted(),
                r.getZonalPastorDirectorsMeeting().name(),
                r.getZonalManagerDirectorsMeeting().name(),
                r.getZonalManagerStrategyMeeting().name(),
                r.getHealingCrusadeSponsorship(),
                r.getTestimonyClarificationConcern(),
                r.getSubmitterEmail(),
                r.getRegionName(),
                r.getStatus(),
                r.getRemittancePurpose(),
                r.getTrumpetsBlown(),
                r.getPopMediaUrl()
        );
    }

    @Transactional
    public ReportDto approveReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus("APPROVED");
        return mapToDto(reportRepository.save(report));
    }

    // ===================== HELPERS =====================
    private String orEmpty(String v) {
        return v == null ? "" : v;
    }

    private int toInt(String v) {
        return (v == null || v.isBlank()) ? 0 : (int) Double.parseDouble(v);
    }

    private double toDoubleSafe(BigDecimal v) {
        return v == null ? 0 : v.doubleValue();
    }

    private int toIntSafe(Integer v) {
        return v == null ? 0 : v;
    }

    private String cell(Row r, int i) {
        return r.getCell(i) == null ? "" : r.getCell(i).toString().trim();
    }

    private String formatEnum(String value) {
        return value
                .trim()
                .replace(" ", "_");
    }
}
