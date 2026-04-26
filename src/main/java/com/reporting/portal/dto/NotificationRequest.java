package com.reporting.portal.dto;

public record NotificationRequest(
        String message,
        String targetRole,
        String userEmail
) {}