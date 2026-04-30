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

        BigDecimal totalPartnershipRemittance,
        Integer newPartnersRecruited,
        Integer testimoniesSubmitted,
        Integer httnmTranslations,
        Integer httnmOutreachesHeld,
        Integer httnmMediaSubmitted,

        String zonalPastorDirectorsMeeting,
        String zonalManagerDirectorsMeeting,
        String zonalManagerStrategyMeeting,

        BigDecimal healingCrusadeSponsorship,
        String testimonyClarificationConcern,
        String regionName,
        
        String remittancePurpose,
        Integer trumpetsBlown,
        String popMediaUrl
) {}