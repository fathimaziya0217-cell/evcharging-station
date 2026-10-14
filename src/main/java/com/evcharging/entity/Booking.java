package com.evcharging.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Booking {
    private Long id;
    private String bookingReference;
    private Long userId;
    private Long stationId;
    private Long chargerId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private BigDecimal estimatedEnergyKwh;
    private BigDecimal estimatedAmount;
    private String bookingStatus; // PENDING_PAYMENT, CONFIRMED, CANCELLED, COMPLETED, EXPIRED
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient joined presentation fields
    private String userName;
    private String userEmail;
    private String userPhone;
    private String stationName;
    private String stationAddress;
    private String stationCity;
    private String chargerIdentifier;
    private String chargerType;
    private String connectorType;
    private BigDecimal chargerPower;

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }

    public Long getChargerId() { return chargerId; }
    public void setChargerId(Long chargerId) { this.chargerId = chargerId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getEstimatedEnergyKwh() { return estimatedEnergyKwh; }
    public void setEstimatedEnergyKwh(BigDecimal estimatedEnergyKwh) { this.estimatedEnergyKwh = estimatedEnergyKwh; }

    public BigDecimal getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(BigDecimal estimatedAmount) { this.estimatedAmount = estimatedAmount; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getStationAddress() { return stationAddress; }
    public void setStationAddress(String stationAddress) { this.stationAddress = stationAddress; }

    public String getStationCity() { return stationCity; }
    public void setStationCity(String stationCity) { this.stationCity = stationCity; }

    public String getChargerIdentifier() { return chargerIdentifier; }
    public void setChargerIdentifier(String chargerIdentifier) { this.chargerIdentifier = chargerIdentifier; }

    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public BigDecimal getChargerPower() { return chargerPower; }
    public void setChargerPower(BigDecimal chargerPower) { this.chargerPower = chargerPower; }
}
