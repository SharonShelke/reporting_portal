package com.reporting.portal.dto;

import java.math.BigDecimal;

public record ReportDto(
    Long id,
    String createdAt,
    String zoneName,
    String zonalManager,
    BigDecimal totalPartnershipRemittance,
    Integer newPartnersRecruited,
    Integer testimoniesSubmitted,
    Integer httnmTranslations,
    Integer httnmOutreaches,
    Integer outreachMediaSubmitted,
    String zonalPastorAttendance,
    String zonalManagerDirectorMeetingAttendance,
    String zonalManagerStrategyMeetingAttendance,
    BigDecimal sponsorshipHealingCrusade,
    String testimonyClarificationConcern,
    String submittedByEmail,
    String regionName,
    String status
) {}
