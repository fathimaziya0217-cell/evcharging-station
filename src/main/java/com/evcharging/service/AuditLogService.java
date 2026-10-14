package com.evcharging.service;

import com.evcharging.entity.AdminAuditLog;
import com.evcharging.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long adminUserId, String action, String entityType, String entityId, String details, String ipAddress) {
        auditLogRepository.logAction(adminUserId, action, entityType, entityId, details, ipAddress);
    }

    public List<AdminAuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findRecent(limit);
    }
}
