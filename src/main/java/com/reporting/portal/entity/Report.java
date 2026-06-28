package com.reporting.portal.entity;

import com.reporting.portal.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "zone_weekly_reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_by", nullable = false, length = 150)
    private String submittedBy;

    @Column(name = "submitter_email", nullable = false, length = 255)
    private String submitterEmail;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "submitted_date", nullable = false)
    private LocalDate submittedDate;

    @Column(name = "submitted_time", nullable = false)
    private LocalTime submittedTime;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "zone_name", nullable = false, length = 150)
    private String zoneName;

    @Column(name = "zonal_manager", nullable = false, length = 150)
    private String zonalManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_pastor_executive_ministers_meeting", nullable = false)
    private AttendanceStatus zonalPastorExecutiveMinistersMeeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_manager_executive_ministers_meeting", nullable = false)
    private AttendanceStatus zonalManagerExecutiveMinistersMeeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_manager_strategy_meeting", nullable = false)
    private AttendanceStatus zonalManagerStrategyMeeting;

    @Column(name = "testimony_clarification_concern", columnDefinition = "TEXT")
    private String testimonyClarificationConcern;

    @Column(name = "region_name", length = 150)
    private String regionName;

    @Column(name = "status", length = 30)
    private String status; // PENDING, APPROVED, CLARIFICATION_NEEDED

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "participation_pray_with_me", columnDefinition = "TEXT")
    private String participationPrayWithMe;

    @Column(name = "total_registration_hslhs")
    private Integer totalRegistrationHslhs;

    @Column(name = "herald_conference", length = 100)
    private String heraldConference;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getParticipationPrayWithMe() { return participationPrayWithMe; }
    public void setParticipationPrayWithMe(String participationPrayWithMe) { this.participationPrayWithMe = participationPrayWithMe; }
    public Integer getTotalRegistrationHslhs() { return totalRegistrationHslhs; }
    public void setTotalRegistrationHslhs(Integer totalRegistrationHslhs) { this.totalRegistrationHslhs = totalRegistrationHslhs; }
    public String getHeraldConference() { return heraldConference; }
    public void setHeraldConference(String heraldConference) { this.heraldConference = heraldConference; }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}