package com.reporting.portal.service;

import com.reporting.portal.dto.PaymentRequest;

import com.reporting.portal.entity.MagazineOrder;
import com.reporting.portal.entity.Payment;
import com.reporting.portal.repository.MagazineOrderRepository;
import com.reporting.portal.repository.PaymentRepository;
import org.springframework.stereotype.Service;



@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MagazineOrderRepository orderRepository;
    private final MagazineOrderService magazineOrderService;

    public PaymentService(
            PaymentRepository paymentRepository,
            MagazineOrderRepository orderRepository,
            MagazineOrderService magazineOrderService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.magazineOrderService = magazineOrderService;
    }

 /*   public Payment submitPayment(Long orderId, PaymentRequest request) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        var payment = new Payment();
        payment.setProofUrl(request.proofUrl());
        payment.setStatus("pending");
        payment.setOrder(order);

        return paymentRepository.save(payment);
    }*/


    public Payment submitPayment(Long orderId, PaymentRequest request) {
        MagazineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Payment payment = new Payment();
        payment.setOrder(order);

        // Payment details
        payment.setMethod(request.getMethod()); // e.g. "paypal"
        payment.setTransactionId(request.getPaymentRef());
        payment.setAmount(order.getTotalAmount());

        // Optional: proof upload (if manual payment)
        payment.setProofUrl(request.getProofUrl());

        // Status handling
        payment.setStatus("COMPLETED");

        var savedPayment = paymentRepository.save(payment);

        // Update order via service to trigger flow logic (deadlines, notifications)
        magazineOrderService.confirmPayment(orderId);

        return savedPayment;
    }

    public Payment getPayment(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for order: " + orderId));
    }
}
