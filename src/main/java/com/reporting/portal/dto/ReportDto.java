package com.reporting.portal.dto;

import java.math.BigDecimal;

public record ReportDto(
    Long id,
    String createdAt,
    String zoneName,
    String zonalManager,
    String zonalPastorExecutiveMinistersMeeting,
    String zonalManagerExecutiveMinistersMeeting,
    String zonalManagerStrategyMeetingAttendance,
    String testimonyClarificationConcern,
    String submittedByEmail,
    String regionName,
    String status,
    String participationPrayWithMe,
    Integer totalRegistrationHslhs,
    String heraldConference
) {}
