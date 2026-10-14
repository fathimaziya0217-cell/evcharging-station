package com.evcharging.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ChargerFormDto {
    private Long id;

    @NotNull(message = "Station ID is required")
    private Long stationId;

    @NotBlank(message = "Charger identifier is required (e.g. GUN-01)")
    private String chargerIdentifier;

    @NotBlank(message = "Charger type is required")
    private String chargerType; // AC Fast Charging, DC Fast Charging

    @NotBlank(message = "Connector type is required")
    private String connectorType; // CCS2, Type 2, CHAdeMO

    @NotNull(message = "Power (kW) is required")
    @DecimalMin(value = "1.0", message = "Power must be at least 1 kW")
    private BigDecimal powerKw;

    @NotNull(message = "Price per kWh is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal pricePerKwh;

    private String availabilityStatus = "AVAILABLE";
    private String operationalStatus = "OPERATIONAL";
    private String description;

    public ChargerFormDto() {}

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
}
