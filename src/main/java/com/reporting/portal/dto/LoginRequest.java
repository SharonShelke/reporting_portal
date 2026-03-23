package com.reporting.portal.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
