package com.evcharging.repository;

import com.evcharging.entity.ApplicationSetting;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SettingsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SettingsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ApplicationSetting> SETTING_ROW_MAPPER = new RowMapper<>() {
        @Override
        public ApplicationSetting mapRow(ResultSet rs, int rowNum) throws SQLException {
            ApplicationSetting s = new ApplicationSetting();
            s.setSettingKey(rs.getString("setting_key"));
            s.setSettingValue(rs.getString("setting_value"));
            s.setDescription(rs.getString("description"));
            if (rs.getTimestamp("updated_at") != null) {
                s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return s;
        }
    };

    public Optional<String> getValue(String key) {
        String sql = "SELECT setting_value FROM application_settings WHERE setting_key = :k";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("k", key), String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ApplicationSetting> findAll() {
        String sql = "SELECT * FROM application_settings ORDER BY setting_key ASC";
        return jdbcTemplate.query(sql, SETTING_ROW_MAPPER);
    }

    public void updateValue(String key, String value) {
        String sql = "UPDATE application_settings SET setting_value = :val, updated_at = NOW() WHERE setting_key = :k";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("val", value)
                .addValue("k", key);
        jdbcTemplate.update(sql, params);
    }
}
