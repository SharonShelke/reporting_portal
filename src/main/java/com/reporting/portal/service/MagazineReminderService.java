package com.reporting.portal.service;

import com.reporting.portal.dto.NotificationRequest;
import com.reporting.portal.entity.MagazineOrder;
import com.reporting.portal.repository.MagazineOrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MagazineReminderService {

    private final MagazineOrderRepository orderRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public MagazineReminderService(
            MagazineOrderRepository orderRepository,
            NotificationService notificationService,
            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void processReminders() {
        checkPaymentReminders();
        checkProductionReminders();
        checkFeedbackReminders();
    }

    private void checkPaymentReminders() {
        List<MagazineOrder> invoicedOrders = orderRepository.findByStatus("INVOICED");
        for (MagazineOrder order : invoicedOrders) {
            if (order.getInvoiceSentAt() != null && 
                order.getInvoiceSentAt().isBefore(LocalDateTime.now().minusDays(3))) {
                
                notificationService.push(new NotificationRequest(
                    "REMINDER: Payment for order #" + order.getId() + " is overdue.", 
                    "zonal", 
                    order.getOrderedBy()
                ));
                emailService.sendSimpleEmail(order.getOrderedBy(), "Payment Reminder", 
                    "Your payment for order #" + order.getId() + " has not been received. Please settle the invoice to begin production.");
            }
        }
    }

    private void checkProductionReminders() {
        List<MagazineOrder> paidOrders = orderRepository.findByStatus("PAID");
        for (MagazineOrder order : paidOrders) {
            LocalDateTime productionStart = order.getProductionStartAt();
            if (productionStart == null) continue;

            // 4-day reminder (Halfway mark)
            if (productionStart.isBefore(LocalDateTime.now().minusDays(4)) && 
                productionStart.isAfter(LocalDateTime.now().minusDays(5))) {
                
                notificationService.push(new NotificationRequest(
                    "PRODUCTION UPDATE: Order #" + order.getId() + " is at the 4-day mark. Printing should be nearly complete.", 
                    "publication", 
                    null
                ));
            }

            // 8-day reminder (Deadline)
            if (productionStart.isBefore(LocalDateTime.now().minusDays(8))) {
                notificationService.push(new NotificationRequest(
                    "URGENT: Order #" + order.getId() + " has exceeded the 8-day production deadline!", 
                    "admin", 
                    null
                ));
                notificationService.push(new NotificationRequest(
                    "DELAY ALERT: Your order #" + order.getId() + " has a minor delay. We are working to expedite it.", 
                    "zonal", 
                    order.getOrderedBy()
                ));
            }
        }
    }

    private void checkFeedbackReminders() {
        // Find delivered orders without feedback
        List<MagazineOrder> delivered = orderRepository.findByStatus("DELIVERED");
        for (MagazineOrder order : delivered) {
            if (!order.getFeedbackReceived()) {
                notificationService.push(new NotificationRequest(
                    "FEEDBACK REQUEST: Please submit pictures and testimonies for order #" + order.getId(), 
                    "zonal", 
                    order.getOrderedBy()
                ));
            }
        }
    }
}
