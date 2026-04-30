package com.reporting.portal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

public class LoginRequest {
    @com.fasterxml.jackson.annotation.JsonAlias({"username", "userEmail", "user_email", "user_name", "email"})
    private String email;

    @com.fasterxml.jackson.annotation.JsonAlias({"pwd", "pass", "userPassword", "user_password", "password"})
    private String password;

    private String loginMethod;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLoginMethod() { return loginMethod; }
    public void setLoginMethod(String loginMethod) { this.loginMethod = loginMethod; }
}
