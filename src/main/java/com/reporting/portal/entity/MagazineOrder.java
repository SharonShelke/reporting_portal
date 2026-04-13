package com.reporting.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "magazine_orders")
@Getter
@Setter
@NoArgsConstructor
public class MagazineOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String zone;
    private String magazineType;
    private Integer quantity;
    private Double totalAmount;
    private String status;
    private String orderedBy;
    private LocalDateTime orderedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}