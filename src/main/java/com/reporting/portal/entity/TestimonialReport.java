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
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class TestimonialReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "submitter_email")
    private String submitterEmail;

    @Column(name = "zone_name")
    private String zoneName;

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

    @Column(name = "status", length = 30)
    private String status = "PENDING";

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "pray_with_me_testimonies", columnDefinition = "TEXT")
    private String prayWithMeTestimonies;

    @Column(name = "translation_testimonies", columnDefinition = "TEXT")
    private String translationTestimonies;

    @Column(name = "partnership_testimonies", columnDefinition = "TEXT")
    private String partnershipTestimonies;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public Integer getTestimoniesCount() { return testimoniesCount; }
    public void setTestimoniesCount(Integer testimoniesCount) { this.testimoniesCount = testimoniesCount; }
    public Integer getBeforeImages() { return beforeImages; }
    public void setBeforeImages(Integer beforeImages) { this.beforeImages = beforeImages; }
    public Integer getAfterImages() { return afterImages; }
    public void setAfterImages(Integer afterImages) { this.afterImages = afterImages; }
    public Integer getDocuments() { return documents; }
    public void setDocuments(Integer documents) { this.documents = documents; }

    public String getPrayWithMeTestimonies() { return prayWithMeTestimonies; }
    public void setPrayWithMeTestimonies(String prayWithMeTestimonies) { this.prayWithMeTestimonies = prayWithMeTestimonies; }
    public String getTranslationTestimonies() { return translationTestimonies; }
    public void setTranslationTestimonies(String translationTestimonies) { this.translationTestimonies = translationTestimonies; }
    public String getPartnershipTestimonies() { return partnershipTestimonies; }
    public void setPartnershipTestimonies(String partnershipTestimonies) { this.partnershipTestimonies = partnershipTestimonies; }
}
