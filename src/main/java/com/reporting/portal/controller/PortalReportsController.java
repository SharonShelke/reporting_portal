package com.reporting.portal.controller;

import com.reporting.portal.entity.*;
import com.reporting.portal.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/portal-reports", "/portal-reports"})
@CrossOrigin(origins = "http://65.0.71.13")
public class PortalReportsController {

    private final PartnershipReportRepository prRepo;
    private final TestimonialReportRepository trRepo;
    private final MagazineReportRepository mrRepo;
    private final OutreachReportRepository orRepo;

    public PortalReportsController(PartnershipReportRepository prRepo, TestimonialReportRepository trRepo,
                                   MagazineReportRepository mrRepo, OutreachReportRepository orRepo) {
        this.prRepo = prRepo;
        this.trRepo = trRepo;
        this.mrRepo = mrRepo;
        this.orRepo = orRepo;
    }

    @PostMapping("/partnership")
    public ResponseEntity<PartnershipReport> submitPartnership(@RequestBody PartnershipReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(prRepo.save(report));
    }
    
    @GetMapping("/partnership")
    public ResponseEntity<List<PartnershipReport>> getPartnership() {
        return ResponseEntity.ok(prRepo.findAll());
    }

    @PostMapping("/testimonials")
    public ResponseEntity<TestimonialReport> submitTestimonials(@RequestBody TestimonialReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(trRepo.save(report));
    }

    @GetMapping("/testimonials")
    public ResponseEntity<List<TestimonialReport>> getTestimonials() {
        return ResponseEntity.ok(trRepo.findAll());
    }

    @PostMapping("/magazine")
    public ResponseEntity<MagazineReport> submitMagazine(@RequestBody MagazineReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(mrRepo.save(report));
    }

    @GetMapping("/magazine")
    public ResponseEntity<List<MagazineReport>> getMagazine() {
        return ResponseEntity.ok(mrRepo.findAll());
    }

    @PostMapping("/outreach")
    public ResponseEntity<OutreachReport> submitOutreach(@RequestBody OutreachReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(orRepo.save(report));
    }

    @GetMapping("/outreach")
    public ResponseEntity<List<OutreachReport>> getOutreach() {
        return ResponseEntity.ok(orRepo.findAll());
    }
}
