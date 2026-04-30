package com.reporting.portal.entity;

import com.reporting.portal.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

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

    @Column(name = "total_partnership_remittance", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPartnershipRemittance;

    @Column(name = "new_partners_recruited", nullable = false)
    private Integer newPartnersRecruited;

    @Column(name = "testimonies_submitted", nullable = false)
    private Integer testimoniesSubmitted;

    @Column(name = "httnm_translations", nullable = false)
    private Integer httnmTranslations;

    @Column(name = "httnm_outreaches_held", nullable = false)
    private Integer httnmOutreachesHeld;

    @Column(name = "httnm_media_submitted", nullable = false)
    private Integer httnmMediaSubmitted;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_pastor_directors_meeting", nullable = false)
    private AttendanceStatus zonalPastorDirectorsMeeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_manager_directors_meeting", nullable = false)
    private AttendanceStatus zonalManagerDirectorsMeeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "zonal_manager_strategy_meeting", nullable = false)
    private AttendanceStatus zonalManagerStrategyMeeting;

    @Column(name = "healing_crusade_sponsorship", nullable = false, precision = 12, scale = 2)
    private BigDecimal healingCrusadeSponsorship;

    @Column(name = "testimony_clarification_concern", columnDefinition = "TEXT")
    private String testimonyClarificationConcern;

    @Column(name = "region_name", length = 150)
    private String regionName;

    @Column(name = "remittance_purpose", columnDefinition = "TEXT")
    private String remittancePurpose;

    @Column(name = "trumpets_blown")
    private Integer trumpetsBlown;

    @Column(name = "pop_media_url", columnDefinition = "TEXT")
    private String popMediaUrl;

    @Column(name = "status", length = 30)
    private String status; // PENDING, APPROVED, CLARIFICATION_NEEDED

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public Integer getNewPartnersRecruited() { return newPartnersRecruited; }
    public void setNewPartnersRecruited(Integer newPartnersRecruited) { this.newPartnersRecruited = newPartnersRecruited; }
    public BigDecimal getTotalPartnershipRemittance() { return totalPartnershipRemittance; }
    public void setTotalPartnershipRemittance(BigDecimal totalPartnershipRemittance) { this.totalPartnershipRemittance = totalPartnershipRemittance; }
    public Integer getTestimoniesSubmitted() { return testimoniesSubmitted; }
    public void setTestimoniesSubmitted(Integer testimoniesSubmitted) { this.testimoniesSubmitted = testimoniesSubmitted; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}