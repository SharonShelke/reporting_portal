package com.reporting.portal.dto;

// The payload sent down to the frontend Table
public record ReportDto(
    String id,
    String rawDate,
    String region,
    String zone,
    String campaign,
    String submittedBy,
    String attendance,
    String notes,
    String status
) {}
