package com.evcharging.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequest {

    @NotNull(message = "Station ID is required")
    private Long stationId;

    @NotNull(message = "Charger ID is required")
    private Long chargerId;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date cannot be in the past")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    // Optional duration override in minutes (e.g. 30, 45, 60)
    private Integer durationMinutes;

    public BookingRequest() {}

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
}
