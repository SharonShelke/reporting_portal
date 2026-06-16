package com.reporting.portal.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "partnership_reports")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnershipReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "submitter_email")
    private String submitterEmail;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "arms", columnDefinition = "TEXT")
    private String arms;

    @Column(name = "total_remittance", precision = 12, scale = 2)
    private BigDecimal totalRemittance;

    @Column(name = "zonal_partnership", columnDefinition = "TEXT")
    private String zonalPartnership;

    @Column(name = "zonal_partnership_details", columnDefinition = "TEXT")
    private String zonalPartnershipDetails;

    @Column(name = "groups_partnership", columnDefinition = "TEXT")
    private String groupsPartnership;

    @Column(name = "churches_partnership", columnDefinition = "TEXT")
    private String churchesPartnership;

    @Column(name = "cell_partnership", columnDefinition = "TEXT")
    private String cellPartnership;

    @Column(name = "healing_crusade_sponsorship", precision = 12, scale = 2)
    private BigDecimal healingCrusadeSponsorship;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "group_pastors_milestones", columnDefinition = "TEXT")
    private String groupPastorsMilestones;

    @Column(name = "sponsored_teenspiration_kidspiration", columnDefinition = "TEXT")
    private String sponsoredTeenspirationKidspiration;

    @Column(name = "sponsored_teenspiration", columnDefinition = "TEXT")
    private String sponsoredTeenspiration;

    @Column(name = "sponsored_kidspiration", columnDefinition = "TEXT")
    private String sponsoredKidspiration;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubmitterEmail() { return submitterEmail; }
    public void setSubmitterEmail(String submitterEmail) { this.submitterEmail = submitterEmail; }
    public String getArms() { return arms; }
    public void setArms(String arms) { this.arms = arms; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public BigDecimal getTotalRemittance() { return totalRemittance; }
    public void setTotalRemittance(BigDecimal totalRemittance) { this.totalRemittance = totalRemittance; }

    public String getZonalPartnership() { return zonalPartnership; }
    public void setZonalPartnership(String zonalPartnership) { this.zonalPartnership = zonalPartnership; }
    public String getZonalPartnershipDetails() { return zonalPartnershipDetails; }
    public void setZonalPartnershipDetails(String zonalPartnershipDetails) { this.zonalPartnershipDetails = zonalPartnershipDetails; }
    public String getGroupsPartnership() { return groupsPartnership; }
    public void setGroupsPartnership(String groupsPartnership) { this.groupsPartnership = groupsPartnership; }
    public String getChurchesPartnership() { return churchesPartnership; }
    public void setChurchesPartnership(String churchesPartnership) { this.churchesPartnership = churchesPartnership; }
    public String getCellPartnership() { return cellPartnership; }
    public void setCellPartnership(String cellPartnership) { this.cellPartnership = cellPartnership; }

    public BigDecimal getHealingCrusadeSponsorship() { return healingCrusadeSponsorship; }
    public void setHealingCrusadeSponsorship(BigDecimal healingCrusadeSponsorship) { this.healingCrusadeSponsorship = healingCrusadeSponsorship; }

    public String getGroupPastorsMilestones() { return groupPastorsMilestones; }
    public void setGroupPastorsMilestones(String groupPastorsMilestones) { this.groupPastorsMilestones = groupPastorsMilestones; }
    public String getSponsoredTeenspirationKidspiration() { return sponsoredTeenspirationKidspiration; }
    public void setSponsoredTeenspirationKidspiration(String sponsoredTeenspirationKidspiration) { this.sponsoredTeenspirationKidspiration = sponsoredTeenspirationKidspiration; }

    public String getSponsoredTeenspiration() { return sponsoredTeenspiration; }
    public void setSponsoredTeenspiration(String sponsoredTeenspiration) { this.sponsoredTeenspiration = sponsoredTeenspiration; }
    public String getSponsoredKidspiration() { return sponsoredKidspiration; }
    public void setSponsoredKidspiration(String sponsoredKidspiration) { this.sponsoredKidspiration = sponsoredKidspiration; }
}
