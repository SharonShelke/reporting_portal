package com.reporting.portal.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

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
    
    private String status; // 'active', 'inactive', 'pending'

    private String inviteToken;

    private LocalDate joinedDate;
    
    @PrePersist
    protected void onCreate() {
        if (this.joinedDate == null) {
            this.joinedDate = LocalDate.now();
        }
    }

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInviteToken() { return inviteToken; }
    public void setInviteToken(String inviteToken) { this.inviteToken = inviteToken; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
}
