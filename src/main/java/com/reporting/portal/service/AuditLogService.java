package com.reporting.portal.service;

import com.reporting.portal.dto.AuditLogDto;
import com.reporting.portal.entity.AuditLog;
import com.reporting.portal.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logActivity(String user, Long userId, String action, String module, String prev, String updated, String details) {
        // Audit logging must never block authentication/requests.
        try {
            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setUserId(userId);
            log.setAction(action);
            log.setModule(module);
            log.setPrev(prev);
            log.setUpdated(updated);
            log.setDetails(details);
            auditLogRepository.save(log);
        } catch (Exception ignored) {
            // Intentionally ignore DB/audit failures (e.g., audit_logs table not created yet).
        }
    }

    public List<AuditLogDto> getAllLogs() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy\nh:mm:ss a");
        return auditLogRepository.findAllByOrderByTimestampDesc().stream().map(log -> new AuditLogDto(
            log.getId(),
            log.getTimestamp() != null ? log.getTimestamp().format(formatter) : "",
            log.getUser(),
            log.getUserId(),
            log.getAction(),
            log.getModule(),
            log.getPrev(),
            log.getUpdated(),
            log.getDetails()
        )).toList();
    }
}
