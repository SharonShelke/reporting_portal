package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "partnership_reports")
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

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public BigDecimal getTotalRemittance() { return totalRemittance; }
    public void setTotalRemittance(BigDecimal totalRemittance) { this.totalRemittance = totalRemittance; }
}
