package com.reporting.portal.service;

import com.reporting.portal.dto.NotificationRequest;
import com.reporting.portal.dto.OrderRequest;
import com.reporting.portal.entity.MagazineOrder;
import com.reporting.portal.repository.MagazineOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MagazineOrderService {

    private final MagazineOrderRepository orderRepository;
    private final NotificationService notificationService;

    public MagazineOrderService(
            MagazineOrderRepository orderRepository,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    public List<MagazineOrder> getAllOrders(String role, String zone) {
        return switch (role) {
            case "zonal" -> orderRepository.findByZone(zone);
            default -> orderRepository.findAll();
        };
    }

    public MagazineOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public MagazineOrder placeOrder(OrderRequest request) {
        var order = new MagazineOrder();
        order.setZone(request.zone());
        order.setMagazineType(request.magazineType());
        order.setQuantity(request.quantity());
        order.setTotalAmount(request.totalAmount());
        order.setOrderedBy(request.orderedBy());
        order.setStatus("pending");

        var saved = orderRepository.save(order);

        notificationService.push(new NotificationRequest(
                "New order placed by zone: " + request.zone(),
                "global",
                null
        ));

        return saved;
    }

    public MagazineOrder updateStatus(Long id, String status) {
        var order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        var order = getOrderById(id);
        if (!order.getStatus().equals("pending")) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }
        orderRepository.deleteById(id);
    }
}