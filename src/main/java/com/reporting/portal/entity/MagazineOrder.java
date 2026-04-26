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

    // Delivery & Financial
    private String deliveryAddress;
    private String country;
    private String stateProvince;
    private String postalCode;
    private String contactEmail;
    private String contactPhone;
    private Double shippingCost;
    private Double taxAmount;
    private Double weight;

    // Timeline
    private LocalDateTime invoiceSentAt;
    private LocalDateTime paymentConfirmedAt;
    private LocalDateTime productionStartAt;
    private LocalDateTime productionDeadline;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    private Boolean feedbackReceived = false;
    @Column(columnDefinition = "TEXT")
    private String delayReport;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}