package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "magazine_reports")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
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

    @Column(name = "is_adult")
    private Boolean isAdult = false;

    @Column(name = "is_teevolution")
    private Boolean isTeevolution = false;

    @Column(name = "is_kids_magazine")
    private Boolean isKidsMagazine = false;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "monthly_minimum_order")
    private Integer monthlyMinimumOrder;

    @Column(name = "amount_paid_magazine", precision = 12, scale = 2)
    private java.math.BigDecimal amountPaidMagazine;

    @Column(name = "adult_copies")
    private Integer adultCopies = 0;

    @Column(name = "adult_languages")
    private String adultLanguages;

    @Column(name = "teens_copies")
    private Integer teensCopies = 0;

    @Column(name = "teens_languages")
    private String teensLanguages;

    @Column(name = "kids_copies")
    private Integer kidsCopies = 0;

    @Column(name = "kids_languages")
    private String kidsLanguages;

    @Column(name = "monthly_copies_ordered")
    private Integer monthlyCopiesOrdered = 0;

    @Column(name = "praise_reports", columnDefinition = "TEXT")
    private String praiseReports;

    @Column(name = "dates_received")
    private String datesReceived;

    @Column(name = "outreach_locations", columnDefinition = "TEXT")
    private String outreachLocations;

    public String getDatesReceived() { return datesReceived; }
    public void setDatesReceived(String datesReceived) { this.datesReceived = datesReceived; }
    public String getOutreachLocations() { return outreachLocations; }
    public void setOutreachLocations(String outreachLocations) { this.outreachLocations = outreachLocations; }

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public Integer getOrdered() { return ordered; }
    public void setOrdered(Integer ordered) { this.ordered = ordered; }
    public Integer getReceived() { return received; }
    public void setReceived(Integer received) { this.received = received; }

    public Boolean getIsAdult() { return isAdult; }
    public void setIsAdult(Boolean isAdult) { this.isAdult = isAdult; }
    public Boolean getIsTeevolution() { return isTeevolution; }
    public void setIsTeevolution(Boolean isTeevolution) { this.isTeevolution = isTeevolution; }
    public Boolean getIsKidsMagazine() { return isKidsMagazine; }
    public void setIsKidsMagazine(Boolean isKidsMagazine) { this.isKidsMagazine = isKidsMagazine; }

    public Integer getMonthlyMinimumOrder() { return monthlyMinimumOrder; }
    public void setMonthlyMinimumOrder(Integer monthlyMinimumOrder) { this.monthlyMinimumOrder = monthlyMinimumOrder; }
    public java.math.BigDecimal getAmountPaidMagazine() { return amountPaidMagazine; }
    public void setAmountPaidMagazine(java.math.BigDecimal amountPaidMagazine) { this.amountPaidMagazine = amountPaidMagazine; }

    public Integer getAdultCopies() { return adultCopies; }
    public void setAdultCopies(Integer adultCopies) { this.adultCopies = adultCopies; }
    public String getAdultLanguages() { return adultLanguages; }
    public void setAdultLanguages(String adultLanguages) { this.adultLanguages = adultLanguages; }

    public Integer getTeensCopies() { return teensCopies; }
    public void setTeensCopies(Integer teensCopies) { this.teensCopies = teensCopies; }
    public String getTeensLanguages() { return teensLanguages; }
    public void setTeensLanguages(String teensLanguages) { this.teensLanguages = teensLanguages; }

    public Integer getKidsCopies() { return kidsCopies; }
    public void setKidsCopies(Integer kidsCopies) { this.kidsCopies = kidsCopies; }
    public String getKidsLanguages() { return kidsLanguages; }
    public void setKidsLanguages(String kidsLanguages) { this.kidsLanguages = kidsLanguages; }

    public Integer getMonthlyCopiesOrdered() { return monthlyCopiesOrdered; }
    public void setMonthlyCopiesOrdered(Integer monthlyCopiesOrdered) { this.monthlyCopiesOrdered = monthlyCopiesOrdered; }

    public String getPraiseReports() { return praiseReports; }
    public void setPraiseReports(String praiseReports) { this.praiseReports = praiseReports; }
}
