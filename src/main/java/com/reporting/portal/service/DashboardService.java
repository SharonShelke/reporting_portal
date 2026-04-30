package com.reporting.portal.service;

import com.reporting.portal.dto.DashboardStatsDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.repository.ReportRepository;
import com.reporting.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public DashboardStatsDto getDashboardStats(String email) {
        Long totalReports = reportRepository.countReports(email);
        Long totalUsers = userRepository.count();
        
        LocalDateTime startOfWeek = LocalDateTime.now()
            .with(DayOfWeek.MONDAY)
            .withHour(0).withMinute(0).withSecond(0);
        Long reportsThisWeek = reportRepository.countReportsSince(startOfWeek, email);
        
        Long totalAttendance = reportRepository.sumTotalAttendance(email);
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
        List<Report> recent = (email == null) 
            ? reportRepository.findTop5ByOrderBySubmittedAtDesc()
            : reportRepository.findTop5BySubmitterEmailOrderBySubmittedAtDesc(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        List<DashboardStatsDto.RecentActivity> activities = recent.stream()
            .map(r -> new DashboardStatsDto.RecentActivity(
                r.getSubmittedBy() != null ? r.getSubmittedBy() : "User",
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
