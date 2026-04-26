package com.reporting.portal.service;

import com.reporting.portal.dto.NotificationRequest;
import com.reporting.portal.dto.OrderRequest;
import com.reporting.portal.entity.MagazineOrder;
import com.reporting.portal.repository.MagazineOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MagazineOrderService {

    private final MagazineOrderRepository orderRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public MagazineOrderService(
            MagazineOrderRepository orderRepository,
            NotificationService notificationService,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
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
        order.setDeliveryAddress(request.deliveryAddress());
        order.setCountry(request.country());
        order.setStateProvince(request.stateProvince());
        order.setPostalCode(request.postalCode());
        order.setContactEmail(request.contactEmail());
        order.setContactPhone(request.contactPhone());
        order.setWeight(request.quantity() * 0.5); // Assume 0.5kg per magazine
        order.setStatus("ORDERED");

        var saved = orderRepository.save(order);

        notificationService.push(new NotificationRequest(
                "New magazine order placed by zone: " + request.zone(),
                "admin",
                null
        ));

        return saved;
    }

    public MagazineOrder generateInvoice(Long id) {
        var order = getOrderById(id);
        
        // Dynamic Tax Calculation based on Country/State
        double taxRate = 0.05; // Base 5%
        String country = order.getCountry();
        if ("USA".equalsIgnoreCase(country)) taxRate = 0.08;
        else if ("UK".equalsIgnoreCase(country)) taxRate = 0.20;
        else if ("Nigeria".equalsIgnoreCase(country)) taxRate = 0.075;
        else if ("South Africa".equalsIgnoreCase(country)) taxRate = 0.15;
        
        order.setTaxAmount(order.getTotalAmount() * taxRate);
        
        // Dynamic Shipping Calculation based on weight and location
        double baseShipping = 15.0; // Minimum base
        double weightRate = 1.5; // $1.5 per kg
        
        // Distance/Region multiplier (Mock logic for different continents)
        double regionMultiplier = 1.0;
        if ("USA".equalsIgnoreCase(country) || "Canada".equalsIgnoreCase(country)) regionMultiplier = 1.2;
        else if ("Australia".equalsIgnoreCase(country)) regionMultiplier = 2.0;
        else if ("Germany".equalsIgnoreCase(country) || "France".equalsIgnoreCase(country)) regionMultiplier = 1.5;
        
        order.setShippingCost((baseShipping + (order.getWeight() * weightRate)) * regionMultiplier);
        
        // Total = Original + Tax + Shipping
        order.setTotalAmount(order.getTotalAmount() + order.getTaxAmount() + order.getShippingCost());
        order.setStatus("INVOICED");
        order.setInvoiceSentAt(LocalDateTime.now());
        
        var saved = orderRepository.save(order);
        
        emailService.sendSimpleEmail(order.getOrderedBy(), "Invoice Generated", 
            "Your invoice for order #" + id + " has been generated. \n" +
            "Tax: " + order.getTaxAmount() + "\n" +
            "Shipping: " + order.getShippingCost() + "\n" +
            "Total Due: " + order.getTotalAmount());
            
        notificationService.push(new NotificationRequest("Invoice generated for order #" + id, "zonal", order.getOrderedBy()));
            
        return saved;
    }

    public MagazineOrder confirmPayment(Long id) {
        var order = getOrderById(id);
        order.setStatus("PAID");
        order.setPaymentConfirmedAt(LocalDateTime.now());
        order.setProductionStartAt(LocalDateTime.now());
        order.setProductionDeadline(LocalDateTime.now().plusDays(8));
        
        var saved = orderRepository.save(order);
        
        // Notifications to Admin, Finance, Publication
        notificationService.push(new NotificationRequest("Payment confirmed for order #" + id, "admin", null));
        notificationService.push(new NotificationRequest("Payment received for order #" + id, "finance", null));
        notificationService.push(new NotificationRequest("New production task (Printing) for order #" + id, "publication", null));
        
        // Notify User
        notificationService.push(new NotificationRequest("Your payment for order #" + id + " has been confirmed. Production started.", "zonal", order.getOrderedBy()));
        emailService.sendSimpleEmail(order.getOrderedBy(), "Payment Receipt", 
            "We have received your payment for order #" + id + ". Production has commenced with an 8-day deadline.");
            
        return saved;
    }

    public MagazineOrder markAsPrintingDone(Long id) {
        var order = getOrderById(id);
        order.setStatus("PRINTED");
        
        var saved = orderRepository.save(order);
        
        notificationService.push(new NotificationRequest("Printing completed for order #" + id + ". Ready for shipping.", "admin", null));
        notificationService.push(new NotificationRequest("Order #" + id + " printing finished.", "publication", null));
        
        // Notify User
        notificationService.push(new NotificationRequest("Printing for your order #" + id + " is complete! Preparing for shipment.", "zonal", order.getOrderedBy()));
        
        return saved;
    }

    public MagazineOrder markAsShipped(Long id) {
        var order = getOrderById(id);
        order.setStatus("SHIPPED");
        order.setShippedAt(LocalDateTime.now());
        
        var saved = orderRepository.save(order);
        
        // Notify Zonal Manager (User)
        notificationService.push(new NotificationRequest("Your order #" + id + " has been shipped!", "zonal", order.getOrderedBy()));
        emailService.sendSimpleEmail(order.getOrderedBy(), "Order Shipped", 
            "Your magazine order #" + id + " has been shipped and is on its way!");
        
        // Notify Admin
        notificationService.push(new NotificationRequest("Order #" + id + " has been shipped by publication department.", "admin", null));
        
        // Notify specific email as requested
        emailService.sendSimpleEmail("sharonshelke7@gmail.com", "Order Shipped - #" + id, 
            "The magazine order #" + id + " for " + order.getZone() + " has been shipped.");
        
        return saved;
    }

    public MagazineOrder updateStatus(Long id, String status) {
        var order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public MagazineOrder cancelOrder(Long id) {
        var order = getOrderById(id);
        order.setStatus("CANCELLED");
        var saved = orderRepository.save(order);
        
        notificationService.push(new NotificationRequest("Order #" + id + " has been cancelled.", "admin", null));
        
        // Notify Zonal Manager (User)
        notificationService.push(new NotificationRequest("Your order #" + id + " has been cancelled.", "zonal", order.getOrderedBy()));
        emailService.sendSimpleEmail(order.getOrderedBy(), "Order Cancelled", 
            "Your magazine order #" + id + " has been cancelled by the administrator.");
        
        // Notify Printing/Shipment Dept
        emailService.sendSimpleEmail("sharonshelke7@gmail.com", "Order CANCELLED - #" + id, 
            "The magazine order #" + id + " for " + order.getZone() + " has been CANCELLED. Please stop any production/shipment.");
        
        return saved;
    }

    public MagazineOrder reportDelay(Long id, String reason) {
        var order = getOrderById(id);
        order.setDelayReport(reason);
        order.setStatus("DELAYED");
        
        var saved = orderRepository.save(order);
        
        notificationService.push(new NotificationRequest("Delay reported for order #" + id + ": " + reason, "admin", null));
        notificationService.push(new NotificationRequest("URGENT: Production delay for order #" + id, "publication", null));
        
        return saved;
    }
}