package com.evcharging.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Charger {
    private Long id;
    private Long stationId;
    private String chargerIdentifier;
    private String chargerType; // AC Fast Charging, DC Fast Charging
    private String connectorType; // CCS2, Type 2, CHAdeMO
    private BigDecimal powerKw;
    private BigDecimal pricePerKwh;
    private String availabilityStatus; // AVAILABLE, OCCUPIED, RESERVED, OFFLINE
    private String operationalStatus; // OPERATIONAL, MAINTENANCE, DECOMMISSIONED
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient station details for view convenience
    private String stationName;
    private String stationCity;
    private String stationAddress;

    public Charger() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }

    public String getChargerIdentifier() { return chargerIdentifier; }
    public void setChargerIdentifier(String chargerIdentifier) { this.chargerIdentifier = chargerIdentifier; }

    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public BigDecimal getPowerKw() { return powerKw; }
    public void setPowerKw(BigDecimal powerKw) { this.powerKw = powerKw; }

    public BigDecimal getPricePerKwh() { return pricePerKwh; }
    public void setPricePerKwh(BigDecimal pricePerKwh) { this.pricePerKwh = pricePerKwh; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getOperationalStatus() { return operationalStatus; }
    public void setOperationalStatus(String operationalStatus) { this.operationalStatus = operationalStatus; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getStationCity() { return stationCity; }
    public void setStationCity(String stationCity) { this.stationCity = stationCity; }

    public String getStationAddress() { return stationAddress; }
    public void setStationAddress(String stationAddress) { this.stationAddress = stationAddress; }
}
