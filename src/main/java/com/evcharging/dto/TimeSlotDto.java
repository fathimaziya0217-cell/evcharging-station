package com.evcharging.dto;

import java.time.LocalTime;

public class TimeSlotDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private String displayLabel;
    private boolean available;
    private String reason; // "Booked", "Past time", etc.

    public TimeSlotDto() {}

    public TimeSlotDto(LocalTime startTime, LocalTime endTime, String displayLabel, boolean available, String reason) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayLabel = displayLabel;
        this.available = available;
        this.reason = reason;
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
