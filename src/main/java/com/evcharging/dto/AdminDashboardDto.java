package com.evcharging.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardDto {
    private long totalUsers;
    private long totalStations;
    private long activeStations;
    private long totalChargers;
    private long availableChargers;
    private long totalBookings;
    private long confirmedBookings;
    private long completedSessions;
    private long successfulPayments;
    private BigDecimal totalPaymentRevenue = BigDecimal.ZERO;

    // Charts aggregate data
    private Map<String, Long> bookingsByDate = new HashMap<>();
    private Map<String, Long> bookingStatusDistribution = new HashMap<>();
    private Map<String, BigDecimal> revenueByDate = new HashMap<>();
    private Map<String, Long> popularStations = new HashMap<>();

    public AdminDashboardDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalStations() { return totalStations; }
    public void setTotalStations(long totalStations) { this.totalStations = totalStations; }

    public long getActiveStations() { return activeStations; }
    public void setActiveStations(long activeStations) { this.activeStations = activeStations; }

    public long getTotalChargers() { return totalChargers; }
    public void setTotalChargers(long totalChargers) { this.totalChargers = totalChargers; }

    public long getAvailableChargers() { return availableChargers; }
    public void setAvailableChargers(long availableChargers) { this.availableChargers = availableChargers; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }

    public long getCompletedSessions() { return completedSessions; }
    public void setCompletedSessions(long completedSessions) { this.completedSessions = completedSessions; }

    public long getSuccessfulPayments() { return successfulPayments; }
    public void setSuccessfulPayments(long successfulPayments) { this.successfulPayments = successfulPayments; }

    public BigDecimal getTotalPaymentRevenue() { return totalPaymentRevenue; }
    public void setTotalPaymentRevenue(BigDecimal totalPaymentRevenue) { this.totalPaymentRevenue = totalPaymentRevenue; }

    public Map<String, Long> getBookingsByDate() { return bookingsByDate; }
    public void setBookingsByDate(Map<String, Long> bookingsByDate) { this.bookingsByDate = bookingsByDate; }

    public Map<String, Long> getBookingStatusDistribution() { return bookingStatusDistribution; }
    public void setBookingStatusDistribution(Map<String, Long> bookingStatusDistribution) { this.bookingStatusDistribution = bookingStatusDistribution; }

    public Map<String, BigDecimal> getRevenueByDate() { return revenueByDate; }
    public void setRevenueByDate(Map<String, BigDecimal> revenueByDate) { this.revenueByDate = revenueByDate; }

    public Map<String, Long> getPopularStations() { return popularStations; }
    public void setPopularStations(Map<String, Long> popularStations) { this.popularStations = popularStations; }
}
