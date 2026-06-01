package com.reporting.portal.service;

import com.reporting.portal.entity.Testimony;
import com.reporting.portal.repository.TestimonyRepository;
import com.reporting.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TestimonyService {

    @Autowired
    private TestimonyRepository testimonyRepository;

    @Autowired
    private UserRepository userRepository;

    public Testimony saveTestimony(Testimony testimony) {
        if (testimony.getUserAvatarUrl() == null && testimony.getUser() != null) {
            String seed = testimony.getUser().getEmail() != null ? testimony.getUser().getEmail() : testimony.getUser().getId().toString();
            testimony.setUserAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + seed);
        }
        testimony.setCreatedAt(LocalDateTime.now());
        testimony.setUpdatedAt(LocalDateTime.now());
        return testimonyRepository.save(testimony);
    }

    public List<Testimony> getApprovedTestimonies() {
        return testimonyRepository.findByStatus("APPROVED");
    }

    public List<Testimony> getTrendingTestimonies() {
        return testimonyRepository.findTrending();
    }

    public List<Testimony> getFeaturedTestimonies(String type) {
        return testimonyRepository.findFeatured(type);
    }

    public Optional<Testimony> getTestimonyById(Long id) {
        return testimonyRepository.findById(id);
    }

    public Testimony approveTestimony(Long id) {
        Testimony testimony = testimonyRepository.findById(id).orElseThrow();
        testimony.setStatus("APPROVED");
        testimony.setUpdatedAt(LocalDateTime.now());
        return testimonyRepository.save(testimony);
    }

    public List<Testimony> getUserTestimonies(Long userId) {
        return testimonyRepository.findByUserId(userId);
    }

    public void addLike(Long testimonyId, Long userId) {
        Testimony testimony = testimonyRepository.findById(testimonyId).orElseThrow();
        testimony.setLikesCount(testimony.getLikesCount() + 1);
        testimonyRepository.save(testimony);
    }

    public void incrementViews(Long testimonyId) {
        Testimony testimony = testimonyRepository.findById(testimonyId).orElseThrow();
        testimony.setViewsCount(testimony.getViewsCount() + 1);
        testimonyRepository.save(testimony);
    }
}
