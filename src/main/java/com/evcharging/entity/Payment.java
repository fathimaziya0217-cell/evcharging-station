package com.evcharging.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private String paymentReference;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String currency; // INR
    private String paymentMethod; // DEMO, UPI, CARD, NETBANKING
    private String paymentStatus; // PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private String failureReason;
    private boolean demo;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient presentation fields
    private String bookingReference;
    private String userEmail;
    private String userName;
    private String stationName;

    public Payment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getGatewayOrderId() { return gatewayOrderId; }
    public void setGatewayOrderId(String gatewayOrderId) { this.gatewayOrderId = gatewayOrderId; }

    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }

    public String getGatewaySignature() { return gatewaySignature; }
    public void setGatewaySignature(String gatewaySignature) { this.gatewaySignature = gatewaySignature; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public boolean isDemo() { return demo; }
    public void setDemo(boolean demo) { this.demo = demo; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
}
