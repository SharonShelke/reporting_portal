package com.reporting.portal.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
