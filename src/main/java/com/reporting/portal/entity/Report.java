package com.reporting.portal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @Column(length = 50, updatable = false, nullable = false)
    private String id;
    
    private LocalDate rawDate;
    private String region;
    private String zone;
    private String campaign;
    private String submittedBy;
    private String attendance;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    private String status;

    @PrePersist
    protected void onCreate() {
        if (this.rawDate == null) {
            this.rawDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = "submitted"; 
        }
    }

    // Standard Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getRawDate() { return rawDate; }
    public void setRawDate(LocalDate rawDate) { this.rawDate = rawDate; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getCampaign() { return campaign; }
    public void setCampaign(String campaign) { this.campaign = campaign; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getAttendance() { return attendance; }
    public void setAttendance(String attendance) { this.attendance = attendance; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
