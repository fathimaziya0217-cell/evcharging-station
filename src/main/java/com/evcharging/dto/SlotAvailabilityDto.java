package com.evcharging.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotAvailabilityDto {
    private Long chargerId;
    private String chargerIdentifier;
    private Long stationId;
    private String stationName;
    private LocalDate date;
    private List<TimeSlotDto> slots = new ArrayList<>();

    public SlotAvailabilityDto() {}

    public Long getChargerId() { return chargerId; }
    public void setChargerId(Long chargerId) { this.chargerId = chargerId; }

    public String getChargerIdentifier() { return chargerIdentifier; }
    public void setChargerIdentifier(String chargerIdentifier) { this.chargerIdentifier = chargerIdentifier; }

    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<TimeSlotDto> getSlots() { return slots; }
    public void setSlots(List<TimeSlotDto> slots) { this.slots = slots; }
}
