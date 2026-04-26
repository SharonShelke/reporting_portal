package com.reporting.portal.service;

import com.reporting.portal.dto.DashboardStatsDto;
import com.reporting.portal.entity.Report;
import com.reporting.portal.repository.MagazineOrderRepository;
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
    private final MagazineOrderRepository orderRepository;
    private final UserRepository userRepository;

    public DashboardStatsDto getDashboardStats() {
        Long totalReports = reportRepository.count();
        Long totalUsers = userRepository.count();
        
        LocalDateTime startOfWeek = LocalDateTime.now()
            .with(DayOfWeek.MONDAY)
            .withHour(0).withMinute(0).withSecond(0);
        Long reportsThisWeek = reportRepository.countReportsSince(startOfWeek);
        
        Double totalFinance = orderRepository.sumTotalAmount();
        if (totalFinance == null) totalFinance = 0.0;
        
        Long financeEntries = orderRepository.count();
        
        Long totalAttendance = reportRepository.sumTotalAttendance();
        if (totalAttendance == null) totalAttendance = 0L;
        
        // Mocking completion rate logic: (submitted / expected)
        // Let's assume 50 expected reports per week
        double expected = 50.0;
        double completionRate = (reportsThisWeek / expected) * 100.0;
        if (completionRate > 100) completionRate = 100.0;

        // Campaign stats (Mocking based on image requirements)
        List<DashboardStatsDto.CampaignStat> campaigns = new ArrayList<>();
        campaigns.add(new DashboardStatsDto.CampaignStat("Spring Outreach", 1000, "#4f46e5"));
        campaigns.add(new DashboardStatsDto.CampaignStat("Easter Campaign", 350, "#818cf8"));
        campaigns.add(new DashboardStatsDto.CampaignStat("Healing Streams", 750, "#6366f1"));

        // Recent activity
        List<Report> recent = reportRepository.findTop5ByOrderBySubmittedAtDesc();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        List<DashboardStatsDto.RecentActivity> activities = recent.stream()
            .map(r -> new DashboardStatsDto.RecentActivity(
                r.getSubmittedBy(),
                r.getZoneName(),
                r.getSubmittedAt().format(formatter)
            )).toList();

        return new DashboardStatsDto(
            totalReports,
            reportsThisWeek,
            totalFinance,
            financeEntries,
            totalAttendance,
            completionRate,
            totalUsers,
            campaigns,
            activities
        );
    }
}
