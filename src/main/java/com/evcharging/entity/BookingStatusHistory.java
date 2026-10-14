package com.evcharging.entity;

import java.time.LocalDateTime;

public class BookingStatusHistory {
    private Long id;
    private Long bookingId;
    private String previousStatus;
    private String newStatus;
    private Long changedByUserId;
    private String remarks;
    private LocalDateTime createdAt;

    public BookingStatusHistory() {}

    public BookingStatusHistory(Long bookingId, String previousStatus, String newStatus, Long changedByUserId, String remarks) {
        this.bookingId = bookingId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedByUserId = changedByUserId;
        this.remarks = remarks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public Long getChangedByUserId() { return changedByUserId; }
    public void setChangedByUserId(Long changedByUserId) { this.changedByUserId = changedByUserId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
