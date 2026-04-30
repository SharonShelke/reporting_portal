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
@Table(name = "magazine_reports")
public class MagazineReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "submitter_email")
    private String submitterEmail;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "ordered_copies")
    private Integer ordered;

    @Column(name = "received_copies")
    private Integer received;

    @Column(name = "receipt_status", length = 20)
    private String receiptStatus;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "sponsored_copies")
    private Integer sponsoredCopies;

    @Column(name = "healing_outreaches")
    private Integer healingOutreaches;

    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;
}
