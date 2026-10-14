package com.evcharging.repository;

import com.evcharging.entity.Booking;
import com.evcharging.entity.BookingStatusHistory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookingRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Booking> BOOKING_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            Booking b = new Booking();
            b.setId(rs.getLong("id"));
            b.setBookingReference(rs.getString("booking_reference"));
            b.setUserId(rs.getLong("user_id"));
            b.setStationId(rs.getLong("station_id"));
            b.setChargerId(rs.getLong("charger_id"));
            b.setBookingDate(rs.getDate("booking_date").toLocalDate());
            b.setStartTime(rs.getTime("start_time").toLocalTime());
            b.setEndTime(rs.getTime("end_time").toLocalTime());
            b.setDurationMinutes(rs.getInt("duration_minutes"));
            b.setEstimatedEnergyKwh(rs.getBigDecimal("estimated_energy_kwh"));
            b.setEstimatedAmount(rs.getBigDecimal("estimated_amount"));
            b.setBookingStatus(rs.getString("booking_status"));
            b.setPaymentStatus(rs.getString("payment_status"));
            b.setCancellationReason(rs.getString("cancellation_reason"));
            if (rs.getTimestamp("cancelled_at") != null) {
                b.setCancelledAt(rs.getTimestamp("cancelled_at").toLocalDateTime());
            }
            if (rs.getTimestamp("created_at") != null) {
                b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                b.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }

            // Joined columns
            try {
                b.setUserName(rs.getString("user_name"));
                b.setUserEmail(rs.getString("user_email"));
                b.setUserPhone(rs.getString("user_phone"));
                b.setStationName(rs.getString("station_name"));
                b.setStationAddress(rs.getString("station_address"));
                b.setStationCity(rs.getString("station_city"));
                b.setChargerIdentifier(rs.getString("charger_identifier"));
                b.setChargerType(rs.getString("charger_type"));
                b.setConnectorType(rs.getString("connector_type"));
                b.setChargerPower(rs.getBigDecimal("power_kw"));
            } catch (SQLException ignored) {}
            return b;
        }
    };

    private static final String BASE_JOIN_QUERY =
            "SELECT b.*, u.full_name as user_name, u.email as user_email, u.phone as user_phone, " +
            "s.name as station_name, s.address as station_address, s.city as station_city, " +
            "c.charger_identifier, c.charger_type, c.connector_type, c.power_kw " +
            "FROM bookings b " +
            "INNER JOIN users u ON b.user_id = u.id " +
            "INNER JOIN stations s ON b.station_id = s.id " +
            "INNER JOIN chargers c ON b.charger_id = c.id ";

    public Optional<Booking> findById(Long id) {
        String sql = BASE_JOIN_QUERY + "WHERE b.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, BOOKING_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Booking> findByReference(String bookingReference) {
        String sql = BASE_JOIN_QUERY + "WHERE b.booking_reference = :ref";
        MapSqlParameterSource params = new MapSqlParameterSource("ref", bookingReference.trim());
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, BOOKING_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Booking> findByUserId(Long userId) {
        String sql = BASE_JOIN_QUERY + "WHERE b.user_id = :userId ORDER BY b.id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, BOOKING_ROW_MAPPER);
    }

    public List<Booking> findByUserIdAndStatus(Long userId, String status) {
        String sql = BASE_JOIN_QUERY + "WHERE b.user_id = :userId AND b.booking_status = :status ORDER BY b.id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", status);
        return jdbcTemplate.query(sql, params, BOOKING_ROW_MAPPER);
    }

    public List<Booking> findAll() {
        String sql = BASE_JOIN_QUERY + "ORDER BY b.id DESC";
        return jdbcTemplate.query(sql, BOOKING_ROW_MAPPER);
    }

    public List<Booking> searchBookings(String query, String status, LocalDate date) {
        StringBuilder sql = new StringBuilder(BASE_JOIN_QUERY).append("WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (query != null && !query.isBlank()) {
            sql.append("AND (LOWER(b.booking_reference) LIKE LOWER(:q) OR LOWER(u.full_name) LIKE LOWER(:q) OR LOWER(u.email) LIKE LOWER(:q) OR LOWER(s.name) LIKE LOWER(:q)) ");
            params.addValue("q", "%" + query.trim() + "%");
        }
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            sql.append("AND b.booking_status = :status ");
            params.addValue("status", status.trim());
        }
        if (date != null) {
            sql.append("AND b.booking_date = :date ");
            params.addValue("date", date);
        }

        sql.append("ORDER BY b.id DESC");
        return jdbcTemplate.query(sql.toString(), params, BOOKING_ROW_MAPPER);
    }

    public List<Booking> findActiveBookingsForChargerAndDate(Long chargerId, LocalDate bookingDate) {
        String sql = "SELECT * FROM bookings WHERE charger_id = :chargerId AND booking_date = :date " +
                "AND booking_status IN ('PENDING_PAYMENT', 'CONFIRMED') ORDER BY start_time ASC";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chargerId", chargerId)
                .addValue("date", bookingDate);
        return jdbcTemplate.query(sql, params, BOOKING_ROW_MAPPER);
    }

    /**
     * Checks whether an overlapping active booking exists for the given charger and time slot.
     */
    public boolean hasOverlappingBooking(Long chargerId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeBookingId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM bookings WHERE charger_id = :chargerId " +
                "AND booking_date = :date AND booking_status IN ('PENDING_PAYMENT', 'CONFIRMED') " +
                "AND (start_time < :endTime AND end_time > :startTime) ");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chargerId", chargerId)
                .addValue("date", date)
                .addValue("startTime", startTime)
                .addValue("endTime", endTime);

        if (excludeBookingId != null) {
            sql.append("AND id != :excludeId ");
            params.addValue("excludeId", excludeBookingId);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
        return count != null && count > 0;
    }

    /**
     * Obtains a row-level lock on the charger row to ensure serializable booking checks.
     */
    public void lockChargerForUpdate(Long chargerId) {
        String sql = "SELECT id FROM chargers WHERE id = :chargerId FOR UPDATE";
        jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("chargerId", chargerId), Long.class);
    }

    public Booking save(Booking b) {
        if (b.getId() == null) {
            String sql = "INSERT INTO bookings (booking_reference, user_id, station_id, charger_id, booking_date, " +
                    "start_time, end_time, duration_minutes, estimated_energy_kwh, estimated_amount, " +
                    "booking_status, payment_status, cancellation_reason, cancelled_at, created_at, updated_at) " +
                    "VALUES (:ref, :userId, :stationId, :chargerId, :date, :startTime, :endTime, :duration, " +
                    ":energy, :amount, :bookingStatus, :paymentStatus, :cancelReason, :cancelledAt, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ref", b.getBookingReference())
                    .addValue("userId", b.getUserId())
                    .addValue("stationId", b.getStationId())
                    .addValue("chargerId", b.getChargerId())
                    .addValue("date", b.getBookingDate())
                    .addValue("startTime", b.getStartTime())
                    .addValue("endTime", b.getEndTime())
                    .addValue("duration", b.getDurationMinutes())
                    .addValue("energy", b.getEstimatedEnergyKwh())
                    .addValue("amount", b.getEstimatedAmount())
                    .addValue("bookingStatus", b.getBookingStatus())
                    .addValue("paymentStatus", b.getPaymentStatus())
                    .addValue("cancelReason", b.getCancellationReason())
                    .addValue("cancelledAt", b.getCancelledAt());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                b.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE bookings SET booking_status = :bookingStatus, payment_status = :paymentStatus, " +
                    "cancellation_reason = :cancelReason, cancelled_at = :cancelledAt, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("bookingStatus", b.getBookingStatus())
                    .addValue("paymentStatus", b.getPaymentStatus())
                    .addValue("cancelReason", b.getCancellationReason())
                    .addValue("cancelledAt", b.getCancelledAt())
                    .addValue("id", b.getId());
            jdbcTemplate.update(sql, params);
        }
        return b;
    }

    public void updateBookingStatus(Long id, String status) {
        String sql = "UPDATE bookings SET booking_status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void updatePaymentStatus(Long id, String status) {
        String sql = "UPDATE bookings SET payment_status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void recordStatusHistory(BookingStatusHistory history) {
        String sql = "INSERT INTO booking_status_history (booking_id, previous_status, new_status, changed_by_user_id, remarks, created_at) " +
                "VALUES (:bookingId, :prev, :next, :changedBy, :remarks, NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bookingId", history.getBookingId())
                .addValue("prev", history.getPreviousStatus())
                .addValue("next", history.getNewStatus())
                .addValue("changedBy", history.getChangedByUserId())
                .addValue("remarks", history.getRemarks());
        jdbcTemplate.update(sql, params);
    }

    public int expireStalePendingBookings(int timeoutMinutes) {
        // Find stale bookings
        String findSql = "SELECT id FROM bookings WHERE booking_status = 'PENDING_PAYMENT' " +
                "AND payment_status = 'PENDING' AND created_at < DATE_SUB(NOW(), INTERVAL :mins MINUTE)";
        List<Long> staleIds = jdbcTemplate.queryForList(findSql, new MapSqlParameterSource("mins", timeoutMinutes), Long.class);

        if (!staleIds.isEmpty()) {
            String updateSql = "UPDATE bookings SET booking_status = 'EXPIRED', updated_at = NOW() WHERE id IN (:ids)";
            jdbcTemplate.update(updateSql, new MapSqlParameterSource("ids", staleIds));
            for (Long bid : staleIds) {
                recordStatusHistory(new BookingStatusHistory(bid, "PENDING_PAYMENT", "EXPIRED", null, "System auto-expired unpaid reservation after timeout"));
            }
        }
        return staleIds.size();
    }

    public long countTotalBookings() {
        String sql = "SELECT COUNT(*) FROM bookings";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    public long countConfirmedBookings() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE booking_status = 'CONFIRMED'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    public long countBookingsByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = :userId";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId), Long.class);
        return count != null ? count : 0L;
    }

    public long countUpcomingBookingsByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = :userId AND booking_status IN ('PENDING_PAYMENT', 'CONFIRMED') AND booking_date >= CURRENT_DATE";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("userId", userId), Long.class);
        return count != null ? count : 0L;
    }
}
