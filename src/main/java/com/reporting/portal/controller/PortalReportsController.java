package com.reporting.portal.controller;

import com.reporting.portal.entity.*;
import com.reporting.portal.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private final ReportBackupRepository backupRepo;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public PortalReportsController(PartnershipReportRepository prRepo, TestimonialReportRepository trRepo,
                                   MagazineReportRepository mrRepo, OutreachReportRepository orRepo,
                                   UserRepository userRepository, ReportBackupRepository backupRepo) {
        this.prRepo = prRepo;
        this.trRepo = trRepo;
        this.mrRepo = mrRepo;
        this.orRepo = orRepo;
        this.userRepository = userRepository;
        this.backupRepo = backupRepo;
    }

    // ── Partnership ───────────────────────────────────────
    @PostMapping("/partnership")
    public ResponseEntity<PartnershipReport> submitPartnership(@RequestBody PartnershipReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        if (report.getStatus() == null) report.setStatus("PENDING");
        return ResponseEntity.ok(prRepo.save(report));
    }

    @GetMapping("/partnership")
    public ResponseEntity<List<PartnershipReport>> getPartnership(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(prRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole()) || "global".equals(user.getRole())) return ResponseEntity.ok(prRepo.findAll());
        return ResponseEntity.ok(prRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/partnership/{id}/approve")
    public ResponseEntity<?> approvePartnership(@PathVariable Long id) {
        return prRepo.findById(id).map(r -> { r.setStatus("APPROVED"); return ResponseEntity.ok(prRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/partnership/{id}/clarification")
    public ResponseEntity<?> clarifyPartnership(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return prRepo.findById(id).map(r -> { r.setStatus("CLARIFICATION_NEEDED"); r.setAdminNote(body.getOrDefault("note", "")); return ResponseEntity.ok(prRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/partnership/{id}")
    public ResponseEntity<?> deletePartnership(@PathVariable Long id) {
        return prRepo.findById(id).map(r -> {
            try {
                String json = objectMapper.writeValueAsString(r);
                backupRepo.save(new ReportBackup(r.getId(), "Partnership", json, "Admin"));
            } catch (Exception e) { System.err.println("Backup failed: " + e.getMessage()); }
            prRepo.delete(r);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Testimonials ──────────────────────────────────────
    @PostMapping("/testimonials")
    public ResponseEntity<TestimonialReport> submitTestimonials(@RequestBody TestimonialReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        if (report.getStatus() == null) report.setStatus("PENDING");
        return ResponseEntity.ok(trRepo.save(report));
    }

    @GetMapping("/testimonials")
    public ResponseEntity<List<TestimonialReport>> getTestimonials(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(trRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole()) || "global".equals(user.getRole())) return ResponseEntity.ok(trRepo.findAll());
        return ResponseEntity.ok(trRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/testimonials/{id}/approve")
    public ResponseEntity<?> approveTestimonial(@PathVariable Long id) {
        return trRepo.findById(id).map(r -> { r.setStatus("APPROVED"); return ResponseEntity.ok(trRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/testimonials/{id}/clarification")
    public ResponseEntity<?> clarifyTestimonial(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return trRepo.findById(id).map(r -> { r.setStatus("CLARIFICATION_NEEDED"); r.setAdminNote(body.getOrDefault("note", "")); return ResponseEntity.ok(trRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/testimonials/{id}")
    public ResponseEntity<?> deleteTestimonial(@PathVariable Long id) {
        return trRepo.findById(id).map(r -> {
            try {
                String json = objectMapper.writeValueAsString(r);
                backupRepo.save(new ReportBackup(r.getId(), "Testimonial", json, "Admin"));
            } catch (Exception e) { System.err.println("Backup failed: " + e.getMessage()); }
            trRepo.delete(r);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Magazine ──────────────────────────────────────────
    @PostMapping("/magazine")
    public ResponseEntity<MagazineReport> submitMagazine(@RequestBody MagazineReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        if (report.getStatus() == null) report.setStatus("PENDING");
        return ResponseEntity.ok(mrRepo.save(report));
    }

    @GetMapping("/magazine")
    public ResponseEntity<List<MagazineReport>> getMagazine(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(mrRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole()) || "global".equals(user.getRole())) return ResponseEntity.ok(mrRepo.findAll());
        return ResponseEntity.ok(mrRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/magazine/{id}/approve")
    public ResponseEntity<?> approveMagazine(@PathVariable Long id) {
        return mrRepo.findById(id).map(r -> { r.setStatus("APPROVED"); return ResponseEntity.ok(mrRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/magazine/{id}/clarification")
    public ResponseEntity<?> clarifyMagazine(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return mrRepo.findById(id).map(r -> { r.setStatus("CLARIFICATION_NEEDED"); r.setAdminNote(body.getOrDefault("note", "")); return ResponseEntity.ok(mrRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/magazine/{id}")
    public ResponseEntity<?> deleteMagazine(@PathVariable Long id) {
        return mrRepo.findById(id).map(r -> {
            try {
                String json = objectMapper.writeValueAsString(r);
                backupRepo.save(new ReportBackup(r.getId(), "Magazine", json, "Admin"));
            } catch (Exception e) { System.err.println("Backup failed: " + e.getMessage()); }
            mrRepo.delete(r);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Outreach ──────────────────────────────────────────
    @PostMapping("/outreach")
    public ResponseEntity<OutreachReport> submitOutreach(@RequestBody OutreachReport report) {
        if (report.getSubmittedDate() == null) report.setSubmittedDate(LocalDate.now());
        if (report.getStatus() == null) report.setStatus("PENDING");
        return ResponseEntity.ok(orRepo.save(report));
    }

    @GetMapping("/outreach")
    public ResponseEntity<List<OutreachReport>> getOutreach(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok(orRepo.findAll());
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.ok(List.of());
        if ("admin".equals(user.getRole()) || "global".equals(user.getRole())) return ResponseEntity.ok(orRepo.findAll());
        return ResponseEntity.ok(orRepo.findAll().stream().filter(r -> email.equals(r.getSubmitterEmail())).collect(Collectors.toList()));
    }

    @PostMapping("/outreach/{id}/approve")
    public ResponseEntity<?> approveOutreach(@PathVariable Long id) {
        return orRepo.findById(id).map(r -> { r.setStatus("APPROVED"); return ResponseEntity.ok(orRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/outreach/{id}/clarification")
    public ResponseEntity<?> clarifyOutreach(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orRepo.findById(id).map(r -> { r.setStatus("CLARIFICATION_NEEDED"); r.setAdminNote(body.getOrDefault("note", "")); return ResponseEntity.ok(orRepo.save(r)); })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/outreach/{id}")
    public ResponseEntity<?> deleteOutreach(@PathVariable Long id) {
        return orRepo.findById(id).map(r -> {
            try {
                String json = objectMapper.writeValueAsString(r);
                backupRepo.save(new ReportBackup(r.getId(), "Outreach", json, "Admin"));
            } catch (Exception e) { System.err.println("Backup failed: " + e.getMessage()); }
            orRepo.delete(r);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}


