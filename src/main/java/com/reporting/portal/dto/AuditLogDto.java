package com.reporting.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private String timestamp;
    private String user;
    private Long userId;
    private String action;
    private String module;
    private String prev;
    private String updated;
    private String details;
}
