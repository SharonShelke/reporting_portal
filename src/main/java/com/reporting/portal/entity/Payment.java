package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proofUrl;
    private String status;
    private LocalDateTime submittedAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "order_id")
    private MagazineOrder order;
}