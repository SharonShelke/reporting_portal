package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "outreach_reports")
public class OutreachReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "submitter_email")
    private String submitterEmail;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "category", length = 150)
    private String category;

    @Column(name = "locations", columnDefinition = "TEXT")
    private String locations;

    @Column(name = "story", columnDefinition = "TEXT")
    private String story;

    @Column(name = "media_count")
    private Integer mediaCount;

    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public Integer getMediaCount() { return mediaCount; }
    public void setMediaCount(Integer mediaCount) { this.mediaCount = mediaCount; }
}
