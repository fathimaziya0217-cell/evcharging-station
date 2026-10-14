package com.evcharging.repository;

import com.evcharging.entity.UserVehicle;
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
public class UserVehicleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserVehicleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<UserVehicle> VEHICLE_ROW_MAPPER = new RowMapper<>() {
        @Override
        public UserVehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserVehicle v = new UserVehicle();
            v.setId(rs.getLong("id"));
            v.setUserId(rs.getLong("user_id"));
            v.setRegistrationNumber(rs.getString("registration_number"));
            v.setMake(rs.getString("make"));
            v.setModel(rs.getString("model"));
            v.setVehicleType(rs.getString("vehicle_type"));
            v.setPreferredConnector(rs.getString("preferred_connector"));
            if (rs.getTimestamp("created_at") != null) {
                v.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                v.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return v;
        }
    };

    public List<UserVehicle> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_vehicles WHERE user_id = :userId ORDER BY id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, VEHICLE_ROW_MAPPER);
    }

    public Optional<UserVehicle> findById(Long id) {
        String sql = "SELECT * FROM user_vehicles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            UserVehicle v = jdbcTemplate.queryForObject(sql, params, VEHICLE_ROW_MAPPER);
            return Optional.ofNullable(v);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public UserVehicle save(UserVehicle v) {
        if (v.getId() == null) {
            String sql = "INSERT INTO user_vehicles (user_id, registration_number, make, model, vehicle_type, preferred_connector, created_at, updated_at) " +
                    "VALUES (:userId, :registrationNumber, :make, :model, :vehicleType, :preferredConnector, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("userId", v.getUserId())
                    .addValue("registrationNumber", v.getRegistrationNumber().trim().toUpperCase())
                    .addValue("make", v.getMake().trim())
                    .addValue("model", v.getModel().trim())
                    .addValue("vehicleType", v.getVehicleType().trim())
                    .addValue("preferredConnector", v.getPreferredConnector().trim());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                v.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE user_vehicles SET registration_number = :registrationNumber, make = :make, model = :model, " +
                    "vehicle_type = :vehicleType, preferred_connector = :preferredConnector, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("registrationNumber", v.getRegistrationNumber().trim().toUpperCase())
                    .addValue("make", v.getMake().trim())
                    .addValue("model", v.getModel().trim())
                    .addValue("vehicleType", v.getVehicleType().trim())
                    .addValue("preferredConnector", v.getPreferredConnector().trim())
                    .addValue("id", v.getId());
            jdbcTemplate.update(sql, params);
        }
        return v;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM user_vehicles WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
