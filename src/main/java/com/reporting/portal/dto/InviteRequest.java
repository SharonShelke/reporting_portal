package com.reporting.portal.dto;

import lombok.Data;

@Data
public class InviteRequest {
    private String email;
    private String role;
    private String region;
}
