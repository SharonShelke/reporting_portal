package com.reporting.portal.dto;

import lombok.Data;

@Data
public class CompleteInviteRequest {
    private String token;
    private String name;
    private String password;
}
