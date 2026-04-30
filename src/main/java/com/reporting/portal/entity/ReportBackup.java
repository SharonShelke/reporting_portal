package com.reporting.portal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_backups")
public class ReportBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalId;
    private String reportType;
    
    @Column(columnDefinition = "TEXT")
    private String reportData;

    private String deletedBy;
    private LocalDateTime deletedAt;

    public ReportBackup() {}

    public ReportBackup(Long originalId, String reportType, String reportData, String deletedBy) {
        this.originalId = originalId;
        this.reportType = reportType;
        this.reportData = reportData;
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOriginalId() { return originalId; }
    public void setOriginalId(Long originalId) { this.originalId = originalId; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getReportData() { return reportData; }
    public void setReportData(String reportData) { this.reportData = reportData; }
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
