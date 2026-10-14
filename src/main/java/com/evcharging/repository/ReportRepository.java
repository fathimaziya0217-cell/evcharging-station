package com.evcharging.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Long> getBookingsByDate(int daysLimit) {
        String sql = "SELECT booking_date, COUNT(*) as cnt FROM bookings " +
                "GROUP BY booking_date ORDER BY booking_date DESC LIMIT :lim";
        MapSqlParameterSource params = new MapSqlParameterSource("lim", daysLimit);
        Map<String, Long> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, params, rs -> {
            result.put(rs.getString("booking_date"), rs.getLong("cnt"));
        });
        return result;
    }

    public Map<String, Long> getBookingStatusDistribution() {
        String sql = "SELECT booking_status, COUNT(*) as cnt FROM bookings GROUP BY booking_status";
        Map<String, Long> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, new MapSqlParameterSource(), rs -> {
            result.put(rs.getString("booking_status"), rs.getLong("cnt"));
        });
        return result;
    }

    public Map<String, BigDecimal> getRevenueByDate(int daysLimit) {
        String sql = "SELECT DATE(paid_at) as p_date, COALESCE(SUM(amount), 0) as total_rev " +
                "FROM payments WHERE payment_status = 'SUCCESS' AND paid_at IS NOT NULL " +
                "GROUP BY DATE(paid_at) ORDER BY p_date DESC LIMIT :lim";
        MapSqlParameterSource params = new MapSqlParameterSource("lim", daysLimit);
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, params, rs -> {
            result.put(rs.getString("p_date"), rs.getBigDecimal("total_rev"));
        });
        return result;
    }

    public Map<String, Long> getPopularStations(int limit) {
        String sql = "SELECT s.name, COUNT(b.id) as cnt FROM stations s " +
                "LEFT JOIN bookings b ON s.id = b.station_id " +
                "GROUP BY s.id, s.name ORDER BY cnt DESC LIMIT :lim";
        MapSqlParameterSource params = new MapSqlParameterSource("lim", limit);
        Map<String, Long> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, params, rs -> {
            result.put(rs.getString("name"), rs.getLong("cnt"));
        });
        return result;
    }

    public List<Map<String, Object>> getStationUtilizationReport() {
        String sql = "SELECT s.id, s.name, s.city, s.status, " +
                "COUNT(DISTINCT c.id) as total_chargers, " +
                "COUNT(b.id) as total_bookings, " +
                "COALESCE(SUM(CASE WHEN b.booking_status = 'COMPLETED' THEN 1 ELSE 0 END), 0) as completed_sessions, " +
                "COALESCE(SUM(CASE WHEN b.payment_status = 'PAID' THEN b.estimated_amount ELSE 0 END), 0) as total_revenue " +
                "FROM stations s " +
                "LEFT JOIN chargers c ON s.id = c.station_id " +
                "LEFT JOIN bookings b ON s.id = b.station_id " +
                "GROUP BY s.id, s.name, s.city, s.status " +
                "ORDER BY total_bookings DESC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getRevenueReport() {
        String sql = "SELECT DATE_FORMAT(p.created_at, '%Y-%m') as month_year, " +
                "COUNT(p.id) as transaction_count, " +
                "COALESCE(SUM(CASE WHEN p.payment_status = 'SUCCESS' THEN p.amount ELSE 0 END), 0) as successful_revenue, " +
                "COALESCE(SUM(CASE WHEN p.payment_status = 'FAILED' THEN 1 ELSE 0 END), 0) as failed_attempts, " +
                "COALESCE(SUM(CASE WHEN p.payment_status = 'REFUNDED' THEN p.amount ELSE 0 END), 0) as refunded_amount " +
                "FROM payments p " +
                "GROUP BY DATE_FORMAT(p.created_at, '%Y-%m') " +
                "ORDER BY month_year DESC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getCancellationReport() {
        String sql = "SELECT b.id, b.booking_reference, u.full_name as user_name, s.name as station_name, " +
                "b.booking_date, b.estimated_amount, b.cancellation_reason, b.cancelled_at " +
                "FROM bookings b " +
                "INNER JOIN users u ON b.user_id = u.id " +
                "INNER JOIN stations s ON b.station_id = s.id " +
                "WHERE b.booking_status = 'CANCELLED' " +
                "ORDER BY b.cancelled_at DESC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getUserRegistrationReport() {
        String sql = "SELECT u.id, u.full_name, u.email, u.phone, u.role, u.is_active, u.created_at, " +
                "COUNT(b.id) as total_bookings, " +
                "COALESCE(SUM(CASE WHEN p.payment_status = 'SUCCESS' THEN p.amount ELSE 0 END), 0) as total_spent " +
                "FROM users u " +
                "LEFT JOIN bookings b ON u.id = b.user_id " +
                "LEFT JOIN payments p ON u.id = p.user_id " +
                "WHERE u.role = 'ROLE_USER' " +
                "GROUP BY u.id, u.full_name, u.email, u.phone, u.role, u.is_active, u.created_at " +
                "ORDER BY u.created_at DESC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }
}
