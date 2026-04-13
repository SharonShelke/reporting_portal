package com.reporting.portal.controller;

import com.reporting.portal.dto.OrderRequest;
import com.reporting.portal.dto.PaymentRequest;
import com.reporting.portal.entity.MagazineOrder;
import com.reporting.portal.entity.Payment;
import com.reporting.portal.service.MagazineOrderService;
import com.reporting.portal.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/magazine/orders")
@CrossOrigin(origins = "*")
public class MagazineOrderController {

    private final MagazineOrderService orderService;
    private final PaymentService paymentService;

    public MagazineOrderController(
            MagazineOrderService orderService,
            PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<MagazineOrder>> getAllOrders(
            @RequestParam String role,
            @RequestParam(required = false) String zone) {
        return ResponseEntity.ok(orderService.getAllOrders(role, zone));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MagazineOrder> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<MagazineOrder> placeOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MagazineOrder> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<Payment> submitPayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.submitPayment(id, request));
    }

    @GetMapping("/{id}/payment")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }
}