package com.reporting.portal.controller;

import com.reporting.portal.entity.Testimony;
import com.reporting.portal.service.TestimonyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/testimonies")
@CrossOrigin(origins = "*")
public class TestimonyController {

    @Autowired
    private TestimonyService testimonyService;

    @PostMapping("/submit")
    public ResponseEntity<Testimony> submitTestimony(@RequestBody Testimony testimony) {
        return ResponseEntity.ok(testimonyService.saveTestimony(testimony));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Testimony>> getApprovedTestimonies() {
        return ResponseEntity.ok(testimonyService.getApprovedTestimonies());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Testimony>> getTrendingTestimonies() {
        return ResponseEntity.ok(testimonyService.getTrendingTestimonies());
    }

    @GetMapping("/featured/{type}")
    public ResponseEntity<List<Testimony>> getFeaturedTestimonies(@PathVariable String type) {
        return ResponseEntity.ok(testimonyService.getFeaturedTestimonies(type));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Testimony> approveTestimony(@PathVariable Long id) {
        return ResponseEntity.ok(testimonyService.approveTestimony(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Testimony>> getUserTestimonies(@PathVariable Long userId) {
        return ResponseEntity.ok(testimonyService.getUserTestimonies(userId));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeTestimony(@PathVariable Long id, @RequestParam Long userId) {
        testimonyService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> viewTestimony(@PathVariable Long id) {
        testimonyService.incrementViews(id);
        return ResponseEntity.ok().build();
    }
}
