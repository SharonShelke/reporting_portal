package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "users")
public class User {

    // Standard Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    
    private String role; // 'global', 'zonal', 'admin', 'finance'
    
    private String region;
    
    private String status; // 'active', 'inactive', 'pending'

    private String inviteToken;

    private LocalDate joinedDate;
    
    private String otpCode;
    private java.time.LocalDateTime otpExpiry;
    
    @PrePersist
    protected void onCreate() {
        if (this.joinedDate == null) {
            this.joinedDate = LocalDate.now();
        }
    }

    public void setId(Long id) { this.id = id; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setRole(String role) { this.role = role; }

    public void setRegion(String region) { this.region = region; }

    public void setStatus(String status) { this.status = status; }

    public void setInviteToken(String inviteToken) { this.inviteToken = inviteToken; }

    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public void setOtpExpiry(java.time.LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
}
