package com.evcharging.repository;

import com.evcharging.entity.Payment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaymentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Payment> PAYMENT_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment p = new Payment();
            p.setId(rs.getLong("id"));
            p.setPaymentReference(rs.getString("payment_reference"));
            p.setBookingId(rs.getLong("booking_id"));
            p.setUserId(rs.getLong("user_id"));
            p.setAmount(rs.getBigDecimal("amount"));
            p.setCurrency(rs.getString("currency"));
            p.setPaymentMethod(rs.getString("payment_method"));
            p.setPaymentStatus(rs.getString("payment_status"));
            p.setGatewayOrderId(rs.getString("gateway_order_id"));
            p.setGatewayPaymentId(rs.getString("gateway_payment_id"));
            p.setGatewaySignature(rs.getString("gateway_signature"));
            p.setFailureReason(rs.getString("failure_reason"));
            p.setDemo(rs.getBoolean("is_demo"));
            if (rs.getTimestamp("paid_at") != null) {
                p.setPaidAt(rs.getTimestamp("paid_at").toLocalDateTime());
            }
            if (rs.getTimestamp("created_at") != null) {
                p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }

            // Joined columns
            try {
                p.setBookingReference(rs.getString("booking_reference"));
                p.setUserName(rs.getString("user_name"));
                p.setUserEmail(rs.getString("user_email"));
                p.setStationName(rs.getString("station_name"));
            } catch (SQLException ignored) {}
            return p;
        }
    };

    private static final String BASE_JOIN_QUERY =
            "SELECT p.*, b.booking_reference, u.full_name as user_name, u.email as user_email, s.name as station_name " +
            "FROM payments p " +
            "INNER JOIN bookings b ON p.booking_id = b.id " +
            "INNER JOIN users u ON p.user_id = u.id " +
            "INNER JOIN stations s ON b.station_id = s.id ";

    public Optional<Payment> findById(Long id) {
        String sql = BASE_JOIN_QUERY + "WHERE p.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, PAYMENT_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Payment> findByReference(String paymentReference) {
        String sql = BASE_JOIN_QUERY + "WHERE p.payment_reference = :ref";
        MapSqlParameterSource params = new MapSqlParameterSource("ref", paymentReference.trim());
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, PAYMENT_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Payment> findByUserId(Long userId) {
        String sql = BASE_JOIN_QUERY + "WHERE p.user_id = :userId ORDER BY p.id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, PAYMENT_ROW_MAPPER);
    }

    public Optional<Payment> findLatestByBookingId(Long bookingId) {
        String sql = BASE_JOIN_QUERY + "WHERE p.booking_id = :bookingId ORDER BY p.id DESC LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource("bookingId", bookingId);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, PAYMENT_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Payment> findAll() {
        String sql = BASE_JOIN_QUERY + "ORDER BY p.id DESC";
        return jdbcTemplate.query(sql, PAYMENT_ROW_MAPPER);
    }

    public List<Payment> searchPayments(String query, String status) {
        StringBuilder sql = new StringBuilder(BASE_JOIN_QUERY).append("WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (query != null && !query.isBlank()) {
            sql.append("AND (LOWER(p.payment_reference) LIKE LOWER(:q) OR LOWER(b.booking_reference) LIKE LOWER(:q) OR LOWER(u.full_name) LIKE LOWER(:q) OR LOWER(u.email) LIKE LOWER(:q)) ");
            params.addValue("q", "%" + query.trim() + "%");
        }
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            sql.append("AND p.payment_status = :status ");
            params.addValue("status", status.trim());
        }

        sql.append("ORDER BY p.id DESC");
        return jdbcTemplate.query(sql.toString(), params, PAYMENT_ROW_MAPPER);
    }

    public Payment save(Payment p) {
        if (p.getId() == null) {
            String sql = "INSERT INTO payments (payment_reference, booking_id, user_id, amount, currency, " +
                    "payment_method, payment_status, gateway_order_id, gateway_payment_id, gateway_signature, " +
                    "failure_reason, is_demo, paid_at, created_at, updated_at) " +
                    "VALUES (:ref, :bookingId, :userId, :amount, :currency, :method, :status, :orderId, " +
                    ":payId, :sig, :reason, :isDemo, :paidAt, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ref", p.getPaymentReference())
                    .addValue("bookingId", p.getBookingId())
                    .addValue("userId", p.getUserId())
                    .addValue("amount", p.getAmount())
                    .addValue("currency", p.getCurrency())
                    .addValue("method", p.getPaymentMethod())
                    .addValue("status", p.getPaymentStatus())
                    .addValue("orderId", p.getGatewayOrderId())
                    .addValue("payId", p.getGatewayPaymentId())
                    .addValue("sig", p.getGatewaySignature())
                    .addValue("reason", p.getFailureReason())
                    .addValue("isDemo", p.isDemo())
                    .addValue("paidAt", p.getPaidAt());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                p.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE payments SET payment_status = :status, gateway_order_id = :orderId, " +
                    "gateway_payment_id = :payId, gateway_signature = :sig, failure_reason = :reason, " +
                    "paid_at = :paidAt, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", p.getPaymentStatus())
                    .addValue("orderId", p.getGatewayOrderId())
                    .addValue("payId", p.getGatewayPaymentId())
                    .addValue("sig", p.getGatewaySignature())
                    .addValue("reason", p.getFailureReason())
                    .addValue("paidAt", p.getPaidAt())
                    .addValue("id", p.getId());
            jdbcTemplate.update(sql, params);
        }
        return p;
    }

    public long countSuccessfulPayments() {
        String sql = "SELECT COUNT(*) FROM payments WHERE payment_status = 'SUCCESS'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    public BigDecimal getTotalSuccessfulRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'SUCCESS'";
        BigDecimal total = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalSpentByUserId(Long userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE user_id = :userId AND payment_status = 'SUCCESS'";
        BigDecimal total = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId), BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }
}
