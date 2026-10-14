package com.evcharging.repository;

import com.evcharging.entity.ChargingSession;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ChargingSessionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChargingSessionRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ChargingSession> SESSION_ROW_MAPPER = new RowMapper<>() {
        @Override
        public ChargingSession mapRow(ResultSet rs, int rowNum) throws SQLException {
            ChargingSession s = new ChargingSession();
            s.setId(rs.getLong("id"));
            s.setSessionReference(rs.getString("session_reference"));
            s.setBookingId(rs.getLong("booking_id"));
            s.setUserId(rs.getLong("user_id"));
            s.setChargerId(rs.getLong("charger_id"));
            s.setSessionStatus(rs.getString("session_status"));
            if (rs.getTimestamp("start_time") != null) {
                s.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            }
            if (rs.getTimestamp("end_time") != null) {
                s.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            }
            s.setEnergyConsumedKwh(rs.getBigDecimal("energy_consumed_kwh"));
            s.setInitialSocPercent(rs.getInt("initial_soc_percent"));
            s.setFinalSocPercent(rs.getInt("final_soc_percent"));
            s.setFinalAmount(rs.getBigDecimal("final_amount"));
            s.setNotes(rs.getString("notes"));
            s.setSimulated(rs.getBoolean("is_simulated"));
            if (rs.getTimestamp("created_at") != null) {
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }

            // Joined columns
            try {
                s.setBookingReference(rs.getString("booking_reference"));
                s.setUserName(rs.getString("user_name"));
                s.setStationName(rs.getString("station_name"));
                s.setChargerIdentifier(rs.getString("charger_identifier"));
            } catch (SQLException ignored) {}
            return s;
        }
    };

    private static final String BASE_JOIN_QUERY =
            "SELECT cs.*, b.booking_reference, u.full_name as user_name, " +
            "st.name as station_name, ch.charger_identifier " +
            "FROM charging_sessions cs " +
            "INNER JOIN bookings b ON cs.booking_id = b.id " +
            "INNER JOIN users u ON cs.user_id = u.id " +
            "INNER JOIN stations st ON b.station_id = st.id " +
            "INNER JOIN chargers ch ON cs.charger_id = ch.id ";

    public Optional<ChargingSession> findById(Long id) {
        String sql = BASE_JOIN_QUERY + "WHERE cs.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, SESSION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<ChargingSession> findByBookingId(Long bookingId) {
        String sql = BASE_JOIN_QUERY + "WHERE cs.booking_id = :bookingId";
        MapSqlParameterSource params = new MapSqlParameterSource("bookingId", bookingId);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, SESSION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ChargingSession> findByUserId(Long userId) {
        String sql = BASE_JOIN_QUERY + "WHERE cs.user_id = :userId ORDER BY cs.id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, SESSION_ROW_MAPPER);
    }

    public List<ChargingSession> findAll() {
        String sql = BASE_JOIN_QUERY + "ORDER BY cs.id DESC";
        return jdbcTemplate.query(sql, SESSION_ROW_MAPPER);
    }

    public ChargingSession save(ChargingSession s) {
        if (s.getId() == null) {
            String sql = "INSERT INTO charging_sessions (session_reference, booking_id, user_id, charger_id, " +
                    "session_status, start_time, end_time, energy_consumed_kwh, initial_soc_percent, " +
                    "final_soc_percent, final_amount, notes, is_simulated, created_at, updated_at) " +
                    "VALUES (:ref, :bookingId, :userId, :chargerId, :status, :startTime, :endTime, " +
                    ":energy, :initSoc, :finalSoc, :amount, :notes, :simulated, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ref", s.getSessionReference())
                    .addValue("bookingId", s.getBookingId())
                    .addValue("userId", s.getUserId())
                    .addValue("chargerId", s.getChargerId())
                    .addValue("status", s.getSessionStatus())
                    .addValue("startTime", s.getStartTime())
                    .addValue("endTime", s.getEndTime())
                    .addValue("energy", s.getEnergyConsumedKwh())
                    .addValue("initSoc", s.getInitialSocPercent())
                    .addValue("finalSoc", s.getFinalSocPercent())
                    .addValue("amount", s.getFinalAmount())
                    .addValue("notes", s.getNotes())
                    .addValue("simulated", s.isSimulated());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                s.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE charging_sessions SET session_status = :status, start_time = :startTime, " +
                    "end_time = :endTime, energy_consumed_kwh = :energy, initial_soc_percent = :initSoc, " +
                    "final_soc_percent = :finalSoc, final_amount = :amount, notes = :notes, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("status", s.getSessionStatus())
                    .addValue("startTime", s.getStartTime())
                    .addValue("endTime", s.getEndTime())
                    .addValue("energy", s.getEnergyConsumedKwh())
                    .addValue("initSoc", s.getInitialSocPercent())
                    .addValue("finalSoc", s.getFinalSocPercent())
                    .addValue("amount", s.getFinalAmount())
                    .addValue("notes", s.getNotes())
                    .addValue("id", s.getId());
            jdbcTemplate.update(sql, params);
        }
        return s;
    }

    public long countCompletedSessions() {
        String sql = "SELECT COUNT(*) FROM charging_sessions WHERE session_status = 'COMPLETED'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }
}
