package com.reporting.portal.repository;

import com.reporting.portal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserEmailOrTargetRole(String userEmail, String role);
    List<Notification> findByUserEmail(String userEmail);
}