package com.reporting.portal.service;

import com.reporting.portal.dto.NotificationRequest;
import com.reporting.portal.entity.Notification;
import com.reporting.portal.repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification push(NotificationRequest request) {
        var notification = new Notification();
        notification.setMessage(request.message());
        notification.setTargetRole(request.targetRole());
        notification.setUserId(request.userId());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long userId, String role) {
        return notificationRepository.findByUserIdOrTargetRole(userId, role);
    }

    public Notification markAsRead(Long id) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        var list = notificationRepository.findByUserId(userId);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}