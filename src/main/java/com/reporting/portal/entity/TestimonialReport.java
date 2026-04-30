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
@Table(name = "testimonial_reports")
public class TestimonialReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "testimony", columnDefinition = "TEXT")
    private String testimony;

    @Column(name = "testimonies_count")
    private Integer testimoniesCount;

    @Column(name = "before_images")
    private Integer beforeImages;

    @Column(name = "after_images")
    private Integer afterImages;

    @Column(name = "documents")
    private Integer documents;

    @Column(name = "status", length = 20)
    private String status = "PENDING";
}
