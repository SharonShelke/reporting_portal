package com.reporting.portal.dto;

// The payload sent by the frontend Submit Button
public record CreateReportRequest(
    String region,
    String zone,
    String campaign,
    String attendance,
    String notes,
    String submittedBy 
) {}

