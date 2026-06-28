package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "outreach_reports")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
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

    @Column(name = "httn_magazine_testimonies_outreaches", columnDefinition = "TEXT")
    private String httnMagazineTestimoniesOutreaches;

    @Column(name = "magazines_used")
    private Integer magazinesUsed = 0;

    @Column(name = "people_involved")
    private Integer peopleInvolved = 0;

    @Column(name = "total_attendance")
    private Integer totalAttendance = 0;

    @Column(name = "souls_saved")
    private Integer soulsSaved = 0;

    @Column(name = "outreach_testimonies", columnDefinition = "TEXT")
    private String outreachTestimonies;

    @Column(name = "follow_up_plan", columnDefinition = "TEXT")
    private String followUpPlan;

    @Column(name = "healing_translations_achieved")
    private Integer healingTranslationsAchieved = 0;

    @Column(name = "healing_outreaches_held")
    private Integer healingOutreachesHeld = 0;

    @Column(name = "healing_media_submitted")
    private Integer healingMediaSubmitted = 0;

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public Integer getMediaCount() { return mediaCount; }
    public void setMediaCount(Integer mediaCount) { this.mediaCount = mediaCount; }

    public String getHttnMagazineTestimoniesOutreaches() { return httnMagazineTestimoniesOutreaches; }
    public void setHttnMagazineTestimoniesOutreaches(String httnMagazineTestimoniesOutreaches) { this.httnMagazineTestimoniesOutreaches = httnMagazineTestimoniesOutreaches; }

    public Integer getMagazinesUsed() { return magazinesUsed; }
    public void setMagazinesUsed(Integer magazinesUsed) { this.magazinesUsed = magazinesUsed; }
    public Integer getPeopleInvolved() { return peopleInvolved; }
    public void setPeopleInvolved(Integer peopleInvolved) { this.peopleInvolved = peopleInvolved; }
    public Integer getTotalAttendance() { return totalAttendance; }
    public void setTotalAttendance(Integer totalAttendance) { this.totalAttendance = totalAttendance; }
    public Integer getSoulsSaved() { return soulsSaved; }
    public void setSoulsSaved(Integer soulsSaved) { this.soulsSaved = soulsSaved; }
    public String getOutreachTestimonies() { return outreachTestimonies; }
    public void setOutreachTestimonies(String outreachTestimonies) { this.outreachTestimonies = outreachTestimonies; }
    public String getFollowUpPlan() { return followUpPlan; }
    public void setFollowUpPlan(String followUpPlan) { this.followUpPlan = followUpPlan; }

    public Integer getHealingTranslationsAchieved() { return healingTranslationsAchieved; }
    public void setHealingTranslationsAchieved(Integer healingTranslationsAchieved) { this.healingTranslationsAchieved = healingTranslationsAchieved; }
    public Integer getHealingOutreachesHeld() { return healingOutreachesHeld; }
    public void setHealingOutreachesHeld(Integer healingOutreachesHeld) { this.healingOutreachesHeld = healingOutreachesHeld; }
    public Integer getHealingMediaSubmitted() { return healingMediaSubmitted; }
    public void setHealingMediaSubmitted(Integer healingMediaSubmitted) { this.healingMediaSubmitted = healingMediaSubmitted; }
}
