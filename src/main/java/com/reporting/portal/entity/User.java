package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

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
    
    @Column(columnDefinition = "VARCHAR(30) DEFAULT 'inactive'")
    private String status = "inactive"; // 'active', 'inactive', 'pending'
    
    private Integer kingchatLoginCount = 0;
    private String kingschatId;

    private String inviteToken;

    private LocalDate joinedDate;
    
    private String otpCode;
    private java.time.LocalDateTime otpExpiry;

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getRegion() { return region; }
    public String getStatus() { return status; }
    public Integer getKingchatLoginCount() { return kingchatLoginCount; }
    public String getKingschatId() { return kingschatId; }
    public String getInviteToken() { return inviteToken; }
    public LocalDate getJoinedDate() { return joinedDate; }
    public String getOtpCode() { return otpCode; }
    public java.time.LocalDateTime getOtpExpiry() { return otpExpiry; }
    
    @PrePersist
    protected void onCreate() {
        if (this.joinedDate == null) {
            this.joinedDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = "inactive";
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

    public void setKingchatLoginCount(Integer kingchatLoginCount) { this.kingchatLoginCount = kingchatLoginCount; }
    public void setKingschatId(String kingschatId) { this.kingschatId = kingschatId; }
}
