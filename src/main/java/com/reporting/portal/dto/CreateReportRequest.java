package com.reporting.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReportRequest(

        String submittedBy,
        String submitterEmail,
        LocalDate submittedDate,
        LocalTime submittedTime,
        LocalDate weekStartDate,

        String zoneName,
        String zonalManager,

        String zonalPastorExecutiveMinistersMeeting,
        String zonalManagerExecutiveMinistersMeeting,
        String zonalManagerStrategyMeeting,

        String testimonyClarificationConcern,
        String regionName,
        
        String popMediaUrl,
        String participationPrayWithMe,
        Integer totalRegistrationHslhs,
        String heraldConference
) {}