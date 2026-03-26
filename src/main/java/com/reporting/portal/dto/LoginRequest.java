package com.reporting.portal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonAlias({"username", "userEmail", "user_email", "user_name", "email"})
    private String email;

    @JsonAlias({"pwd", "pass", "userPassword", "user_password", "password"})
    private String password;
}
