package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "testimony_likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"testimony_id", "user_id"})})
public class TestimonyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testimony_id")
    private Testimony testimony;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
