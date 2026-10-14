package com.evcharging.service;

import com.evcharging.dto.AdminDashboardDto;
import com.evcharging.repository.*;
import com.evcharging.util.CsvExportUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final ChargerRepository chargerRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ChargingSessionRepository sessionRepository;
    private final ReportRepository reportRepository;

    public AdminReportService(UserRepository userRepository,
                              StationRepository stationRepository,
                              ChargerRepository chargerRepository,
                              BookingRepository bookingRepository,
                              PaymentRepository paymentRepository,
                              ChargingSessionRepository sessionRepository,
                              ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.chargerRepository = chargerRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.sessionRepository = sessionRepository;
        this.reportRepository = reportRepository;
    }

    public AdminDashboardDto getDashboardSummary() {
        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(userRepository.countUsers());
        dto.setTotalStations(stationRepository.countTotalStations());
        dto.setActiveStations(stationRepository.countActiveStations());
        dto.setTotalChargers(chargerRepository.countTotalChargers());
        dto.setAvailableChargers(chargerRepository.countAvailableChargers());
        dto.setTotalBookings(bookingRepository.countTotalBookings());
        dto.setConfirmedBookings(bookingRepository.countConfirmedBookings());
        dto.setCompletedSessions(sessionRepository.countCompletedSessions());
        dto.setSuccessfulPayments(paymentRepository.countSuccessfulPayments());
        dto.setTotalPaymentRevenue(paymentRepository.getTotalSuccessfulRevenue());

        // Aggregate chart data
        dto.setBookingsByDate(reportRepository.getBookingsByDate(14));
        dto.setBookingStatusDistribution(reportRepository.getBookingStatusDistribution());
        dto.setRevenueByDate(reportRepository.getRevenueByDate(14));
        dto.setPopularStations(reportRepository.getPopularStations(6));

        return dto;
    }

    public List<Map<String, Object>> getStationUtilizationReport() {
        return reportRepository.getStationUtilizationReport();
    }

    public List<Map<String, Object>> getRevenueReport() {
        return reportRepository.getRevenueReport();
    }

    public List<Map<String, Object>> getCancellationReport() {
        return reportRepository.getCancellationReport();
    }

    public List<Map<String, Object>> getUserRegistrationReport() {
        return reportRepository.getUserRegistrationReport();
    }

    public String exportStationUtilizationCsv() {
        List<Map<String, Object>> data = getStationUtilizationReport();
        List<String> headers = Arrays.asList("Station ID", "Station Name", "City", "Status", "Total Chargers", "Total Bookings", "Completed Sessions", "Revenue (INR)");
        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> map : data) {
            rows.add(Arrays.asList(
                    String.valueOf(map.get("id")),
                    String.valueOf(map.get("name")),
                    String.valueOf(map.get("city")),
                    String.valueOf(map.get("status")),
                    String.valueOf(map.get("total_chargers")),
                    String.valueOf(map.get("total_bookings")),
                    String.valueOf(map.get("completed_sessions")),
                    String.valueOf(map.get("total_revenue"))
            ));
        }
        return CsvExportUtil.buildCsv(headers, rows);
    }

    public String exportRevenueCsv() {
        List<Map<String, Object>> data = getRevenueReport();
        List<String> headers = Arrays.asList("Month", "Transaction Count", "Successful Revenue (INR)", "Failed Attempts", "Refunded (INR)");
        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> map : data) {
            rows.add(Arrays.asList(
                    String.valueOf(map.get("month_year")),
                    String.valueOf(map.get("transaction_count")),
                    String.valueOf(map.get("successful_revenue")),
                    String.valueOf(map.get("failed_attempts")),
                    String.valueOf(map.get("refunded_amount"))
            ));
        }
        return CsvExportUtil.buildCsv(headers, rows);
    }

    public String exportCancellationsCsv() {
        List<Map<String, Object>> data = getCancellationReport();
        List<String> headers = Arrays.asList("Booking ID", "Reference", "User Name", "Station Name", "Booking Date", "Amount (INR)", "Reason", "Cancelled At");
        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> map : data) {
            rows.add(Arrays.asList(
                    String.valueOf(map.get("id")),
                    String.valueOf(map.get("booking_reference")),
                    String.valueOf(map.get("user_name")),
                    String.valueOf(map.get("station_name")),
                    String.valueOf(map.get("booking_date")),
                    String.valueOf(map.get("estimated_amount")),
                    String.valueOf(map.get("cancellation_reason")),
                    String.valueOf(map.get("cancelled_at"))
            ));
        }
        return CsvExportUtil.buildCsv(headers, rows);
    }

    public String exportUsersCsv() {
        List<Map<String, Object>> data = getUserRegistrationReport();
        List<String> headers = Arrays.asList("User ID", "Full Name", "Email", "Phone", "Status", "Registered At", "Total Bookings", "Total Spent (INR)");
        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> map : data) {
            rows.add(Arrays.asList(
                    String.valueOf(map.get("id")),
                    String.valueOf(map.get("full_name")),
                    String.valueOf(map.get("email")),
                    String.valueOf(map.get("phone")),
                    String.valueOf(map.get("is_active")),
                    String.valueOf(map.get("created_at")),
                    String.valueOf(map.get("total_bookings")),
                    String.valueOf(map.get("total_spent"))
            ));
        }
        return CsvExportUtil.buildCsv(headers, rows);
    }
}
