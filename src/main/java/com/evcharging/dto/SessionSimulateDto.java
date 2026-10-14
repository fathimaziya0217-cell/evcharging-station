package com.evcharging.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SessionSimulateDto {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Simulated energy consumed (kWh) is required")
    @DecimalMin(value = "0.1", message = "Energy consumed must be greater than zero")
    private BigDecimal energyConsumedKwh;

    private Integer initialSocPercent = 20;
    private Integer finalSocPercent = 80;
    private String notes;

    public SessionSimulateDto() {}

    public Long getSessionId() { return id(); }
    public Long id() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public BigDecimal getEnergyConsumedKwh() { return energyConsumedKwh; }
    public void setEnergyConsumedKwh(BigDecimal energyConsumedKwh) { this.energyConsumedKwh = energyConsumedKwh; }

    public Integer getInitialSocPercent() { return initialSocPercent; }
    public void setInitialSocPercent(Integer initialSocPercent) { this.initialSocPercent = initialSocPercent; }

    public Integer getFinalSocPercent() { return finalSocPercent; }
    public void setFinalSocPercent(Integer finalSocPercent) { this.finalSocPercent = finalSocPercent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
