package com.evcharging.entity;

import java.time.LocalDateTime;

public class AdminAuditLog {
    private Long id;
    private Long adminUserId;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;

    // Transient joined presentation field
    private String adminName;

    public AdminAuditLog() {}

    public AdminAuditLog(Long adminUserId, String action, String entityType, String entityId, String details, String ipAddress) {
        this.adminUserId = adminUserId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Long adminUserId) { this.adminUserId = adminUserId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}
