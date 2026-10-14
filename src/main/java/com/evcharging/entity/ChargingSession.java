package com.evcharging.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChargingSession {
    private Long id;
    private String sessionReference;
    private Long bookingId;
    private Long userId;
    private Long chargerId;
    private String sessionStatus; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal energyConsumedKwh;
    private Integer initialSocPercent;
    private Integer finalSocPercent;
    private BigDecimal finalAmount;
    private String notes;
    private boolean simulated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient presentation fields
    private String bookingReference;
    private String userName;
    private String stationName;
    private String chargerIdentifier;

    public ChargingSession() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionReference() { return sessionReference; }
    public void setSessionReference(String sessionReference) { this.sessionReference = sessionReference; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getChargerId() { return chargerId; }
    public void setChargerId(Long chargerId) { this.chargerId = chargerId; }

    public String getSessionStatus() { return sessionStatus; }
    public void setSessionStatus(String sessionStatus) { this.sessionStatus = sessionStatus; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getEnergyConsumedKwh() { return energyConsumedKwh; }
    public void setEnergyConsumedKwh(BigDecimal energyConsumedKwh) { this.energyConsumedKwh = energyConsumedKwh; }

    public Integer getInitialSocPercent() { return initialSocPercent; }
    public void setInitialSocPercent(Integer initialSocPercent) { this.initialSocPercent = initialSocPercent; }

    public Integer getFinalSocPercent() { return finalSocPercent; }
    public void setFinalSocPercent(Integer finalSocPercent) { this.finalSocPercent = finalSocPercent; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isSimulated() { return simulated; }
    public void setSimulated(boolean simulated) { this.simulated = simulated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getChargerIdentifier() { return chargerIdentifier; }
    public void setChargerIdentifier(String chargerIdentifier) { this.chargerIdentifier = chargerIdentifier; }
}
