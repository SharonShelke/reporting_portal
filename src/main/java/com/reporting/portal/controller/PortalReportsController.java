package com.reporting.portal.controller;

import com.reporting.portal.entity.*;
import com.reporting.portal.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/portal-reports", "/portal-reports"})
@CrossOrigin(origins = "http://65.0.71.13")
public class PortalReportsController {

    private final PartnershipReportRepository prRepo;
    private final TestimonialReportRepository trRepo;
    private final MagazineReportRepository mrRepo;
    private final OutreachReportRepository orRepo;
    private final UserRepository userRepository;

    public PortalReportsController(PartnershipReportRepository prRepo, TestimonialReportRepository trRepo,
                                   MagazineReportRepository mrRepo, OutreachReportRepository orRepo,
                                   UserRepository userRepository) {
        this.prRepo = prRepo;
        this.trRepo = trRepo;
        this.mrRepo = mrRepo;
        this.orRepo = orRepo;
        this.userRepository = userRepository;
    }

    @PostMapping("/partnership")
    public ResponseEntity<PartnershipReport> submitPartnership(@RequestBody PartnershipReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(prRepo.save(report));
    }
    
    @GetMapping("/partnership")
    public ResponseEntity<List<PartnershipReport>> getPartnership(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(prRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole())) return ResponseEntity.ok(prRepo.findAll());
        return ResponseEntity.ok(prRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/testimonials")
    public ResponseEntity<TestimonialReport> submitTestimonials(@RequestBody TestimonialReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(trRepo.save(report));
    }

    @GetMapping("/testimonials")
    public ResponseEntity<List<TestimonialReport>> getTestimonials(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(trRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole())) return ResponseEntity.ok(trRepo.findAll());
        return ResponseEntity.ok(trRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/magazine")
    public ResponseEntity<MagazineReport> submitMagazine(@RequestBody MagazineReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(mrRepo.save(report));
    }

    @GetMapping("/magazine")
    public ResponseEntity<List<MagazineReport>> getMagazine(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(mrRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole())) return ResponseEntity.ok(mrRepo.findAll());
        return ResponseEntity.ok(mrRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/outreach")
    public ResponseEntity<OutreachReport> submitOutreach(@RequestBody OutreachReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        return ResponseEntity.ok(orRepo.save(report));
    }

    @GetMapping("/outreach")
    public ResponseEntity<List<OutreachReport>> getOutreach(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(orRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole())) return ResponseEntity.ok(orRepo.findAll());
        return ResponseEntity.ok(orRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }
}
