package com.reporting.portal.dto;

import java.util.List;
import java.util.Map;

public record DashboardStatsDto(
    Long totalReports,
    Long reportsThisWeek,
    Double totalFinance,
    Long financeEntries,
    Long totalAttendance,
    Double completionRate,
    Long totalUsers,
    List<CampaignStat> campaignPerformance,
    List<RecentActivity> recentActivity
) {
    public record CampaignStat(String name, Integer attendance, String color) {}
    public record RecentActivity(String user, String zone, String date) {}
}
