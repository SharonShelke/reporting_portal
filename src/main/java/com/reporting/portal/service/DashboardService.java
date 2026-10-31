package com.reporting.portal.service;

import com.reporting.portal.dto.DashboardStatsDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.repository.ReportRepository;
import com.reporting.portal.repository.UserRepository;
import com.reporting.portal.repository.PartnershipReportRepository;
import com.reporting.portal.repository.TestimonialReportRepository;
import com.reporting.portal.repository.MagazineReportRepository;
import com.reporting.portal.repository.OutreachReportRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PartnershipReportRepository prRepo;
    private final TestimonialReportRepository trRepo;
    private final MagazineReportRepository mrRepo;
    private final OutreachReportRepository orRepo;

    public DashboardService(ReportRepository reportRepository, UserRepository userRepository,
                            PartnershipReportRepository prRepo, TestimonialReportRepository trRepo,
                            MagazineReportRepository mrRepo, OutreachReportRepository orRepo) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.prRepo = prRepo;
        this.trRepo = trRepo;
        this.mrRepo = mrRepo;
        this.orRepo = orRepo;
    }

    public DashboardStatsDto getDashboardStats(String email) {
        String cleanEmail = (email != null && !email.trim().isEmpty() && !email.trim().equalsIgnoreCase("undefined") && !email.trim().equalsIgnoreCase("null"))
                ? email.trim().toLowerCase()
                : null;

        // Admins and Global Managers see all reports across the entire organization
        if (cleanEmail != null) {
            var userOpt = userRepository.findByEmail(cleanEmail);
            if (userOpt.isPresent()) {
                String role = userOpt.get().getRole();
                if ("admin".equalsIgnoreCase(role) || "global".equalsIgnoreCase(role)) {
                    cleanEmail = null;
                }
            }
        }

        Long zCount = (cleanEmail == null) ? reportRepository.count() : reportRepository.countReports(cleanEmail);
        Long pCount = (cleanEmail == null) ? prRepo.count() : prRepo.countReports(cleanEmail);
        Long tCount = (cleanEmail == null) ? trRepo.count() : trRepo.countReports(cleanEmail);
        Long mCount = (cleanEmail == null) ? mrRepo.count() : mrRepo.countReports(cleanEmail);
        Long oCount = (cleanEmail == null) ? orRepo.count() : orRepo.countReports(cleanEmail);
        Long totalReports = (zCount != null ? zCount : 0L)
                          + (pCount != null ? pCount : 0L)
                          + (tCount != null ? tCount : 0L)
                          + (mCount != null ? mCount : 0L)
                          + (oCount != null ? oCount : 0L);
        Long totalUsers = userRepository.count();
        
        LocalDateTime startOfWeek = LocalDateTime.now()
            .with(DayOfWeek.MONDAY)
            .withHour(0).withMinute(0).withSecond(0);
        Long reportsThisWeek = reportRepository.countReportsSince(startOfWeek, cleanEmail);
        
        Long totalAttendance = reportRepository.sumTotalAttendance(cleanEmail);
        if (totalAttendance == null) totalAttendance = 0L;
        
        // Mocking completion rate logic: (submitted / expected)
        double expected = 50.0;
        double completionRate = (reportsThisWeek / expected) * 100.0;
        if (completionRate > 100) completionRate = 100.0;

        // Campaign stats (Mocking based on image requirements)
        List<DashboardStatsDto.CampaignStat> campaigns = new ArrayList<>();
        campaigns.add(new DashboardStatsDto.CampaignStat("Spring Outreach", (int)(totalAttendance * 0.4), "#4f46e5"));
        campaigns.add(new DashboardStatsDto.CampaignStat("Easter Campaign", (int)(totalAttendance * 0.2), "#818cf8"));
        campaigns.add(new DashboardStatsDto.CampaignStat("Healing Streams", (int)(totalAttendance * 0.3), "#6366f1"));

        // Recent activity
        List<Report> recent = (cleanEmail == null) 
            ? reportRepository.findTop5ByOrderBySubmittedAtDesc()
            : reportRepository.findTop5BySubmitterEmailOrderBySubmittedAtDesc(cleanEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        List<DashboardStatsDto.RecentActivity> activities = recent.stream()
            .map(r -> new DashboardStatsDto.RecentActivity(
                r.getSubmittedBy() != null ? r.getSubmittedBy() : (r.getZonalManager() != null ? r.getZonalManager() : "User"),
                r.getZoneName() != null ? r.getZoneName() : "Zone",
                r.getSubmittedAt() != null ? r.getSubmittedAt().format(formatter) : "—"
            )).toList();

        return new DashboardStatsDto(
            totalReports,
            reportsThisWeek,
            totalAttendance,
            completionRate,
            totalUsers,
            campaigns,
            activities
        );
    }
}
