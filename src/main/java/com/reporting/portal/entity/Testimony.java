package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "testimonies")
public class Testimony {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String category; // Healing Streams, Partnership, etc.

    @Column(name = "media_type")
    private String mediaType; // VIDEO, AUDIO, PHOTO, TEXT

    @ElementCollection
    @CollectionTable(name = "testimony_media_urls", joinColumns = @JoinColumn(name = "testimony_id"))
    @Column(name = "url")
    private List<String> mediaUrls;

    @Column(name = "user_avatar_url")
    private String userAvatarUrl;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, FLAGGED

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "featured_type")
    private String featuredType; // DAILY, WEEKLY, MONTHLY

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
