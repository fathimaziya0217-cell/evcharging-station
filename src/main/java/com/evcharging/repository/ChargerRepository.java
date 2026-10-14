package com.evcharging.repository;

import com.evcharging.entity.Charger;
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
public class ChargerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChargerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Charger> CHARGER_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Charger mapRow(ResultSet rs, int rowNum) throws SQLException {
            Charger c = new Charger();
            c.setId(rs.getLong("id"));
            c.setStationId(rs.getLong("station_id"));
            c.setChargerIdentifier(rs.getString("charger_identifier"));
            c.setChargerType(rs.getString("charger_type"));
            c.setConnectorType(rs.getString("connector_type"));
            c.setPowerKw(rs.getBigDecimal("power_kw"));
            c.setPricePerKwh(rs.getBigDecimal("price_per_kwh"));
            c.setAvailabilityStatus(rs.getString("availability_status"));
            c.setOperationalStatus(rs.getString("operational_status"));
            c.setDescription(rs.getString("description"));
            if (rs.getTimestamp("created_at") != null) {
                c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            // Optional station columns if joined
            try {
                c.setStationName(rs.getString("station_name"));
                c.setStationCity(rs.getString("station_city"));
                c.setStationAddress(rs.getString("station_address"));
            } catch (SQLException ignored) {}
            return c;
        }
    };

    public Optional<Charger> findById(Long id) {
        String sql = "SELECT c.*, s.name as station_name, s.city as station_city, s.address as station_address " +
                "FROM chargers c INNER JOIN stations s ON c.station_id = s.id WHERE c.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Charger charger = jdbcTemplate.queryForObject(sql, params, CHARGER_ROW_MAPPER);
            return Optional.ofNullable(charger);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Charger> findByStationId(Long stationId) {
        String sql = "SELECT c.*, s.name as station_name, s.city as station_city, s.address as station_address " +
                "FROM chargers c INNER JOIN stations s ON c.station_id = s.id WHERE c.station_id = :stationId ORDER BY c.id ASC";
        MapSqlParameterSource params = new MapSqlParameterSource("stationId", stationId);
        return jdbcTemplate.query(sql, params, CHARGER_ROW_MAPPER);
    }

    public List<Charger> findAll() {
        String sql = "SELECT c.*, s.name as station_name, s.city as station_city, s.address as station_address " +
                "FROM chargers c INNER JOIN stations s ON c.station_id = s.id ORDER BY c.id DESC";
        return jdbcTemplate.query(sql, CHARGER_ROW_MAPPER);
    }

    public Charger save(Charger c) {
        if (c.getId() == null) {
            String sql = "INSERT INTO chargers (station_id, charger_identifier, charger_type, connector_type, power_kw, " +
                    "price_per_kwh, availability_status, operational_status, description, created_at, updated_at) " +
                    "VALUES (:stationId, :identifier, :chargerType, :connectorType, :powerKw, :pricePerKwh, " +
                    ":availStatus, :operStatus, :description, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("stationId", c.getStationId())
                    .addValue("identifier", c.getChargerIdentifier().trim())
                    .addValue("chargerType", c.getChargerType().trim())
                    .addValue("connectorType", c.getConnectorType().trim())
                    .addValue("powerKw", c.getPowerKw())
                    .addValue("pricePerKwh", c.getPricePerKwh())
                    .addValue("availStatus", c.getAvailabilityStatus())
                    .addValue("operStatus", c.getOperationalStatus())
                    .addValue("description", c.getDescription());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                c.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE chargers SET station_id = :stationId, charger_identifier = :identifier, " +
                    "charger_type = :chargerType, connector_type = :connectorType, power_kw = :powerKw, " +
                    "price_per_kwh = :pricePerKwh, availability_status = :availStatus, operational_status = :operStatus, " +
                    "description = :description, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("stationId", c.getStationId())
                    .addValue("identifier", c.getChargerIdentifier().trim())
                    .addValue("chargerType", c.getChargerType().trim())
                    .addValue("connectorType", c.getConnectorType().trim())
                    .addValue("powerKw", c.getPowerKw())
                    .addValue("pricePerKwh", c.getPricePerKwh())
                    .addValue("availStatus", c.getAvailabilityStatus())
                    .addValue("operStatus", c.getOperationalStatus())
                    .addValue("description", c.getDescription())
                    .addValue("id", c.getId());
            jdbcTemplate.update(sql, params);
        }
        return c;
    }

    public void updateAvailability(Long id, String status) {
        String sql = "UPDATE chargers SET availability_status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void updateOperationalStatus(Long id, String status) {
        String sql = "UPDATE chargers SET operational_status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public void updatePricing(Long id, BigDecimal pricePerKwh) {
        String sql = "UPDATE chargers SET price_per_kwh = :price, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("price", pricePerKwh)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public boolean hasHistoricalBookings(Long chargerId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE charger_id = :chargerId";
        Integer count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("chargerId", chargerId), Integer.class);
        return count != null && count > 0;
    }

    public long countTotalChargers() {
        String sql = "SELECT COUNT(*) FROM chargers";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    public long countAvailableChargers() {
        String sql = "SELECT COUNT(*) FROM chargers WHERE availability_status = 'AVAILABLE' AND operational_status = 'OPERATIONAL'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }
}
