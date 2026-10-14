package com.evcharging.repository;

import com.evcharging.entity.User;
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
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(rs.getString("role"));
            user.setActive(rs.getBoolean("is_active"));
            if (rs.getTimestamp("created_at") != null) {
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return user;
        }
    };

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, USER_ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(:email)";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email.trim());
        try {
            User user = jdbcTemplate.queryForObject(sql, params, USER_ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(:email)";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email.trim());
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public User save(User user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO users (full_name, email, phone, password_hash, role, is_active, created_at, updated_at) " +
                    "VALUES (:fullName, :email, :phone, :passwordHash, :role, :isActive, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("fullName", user.getFullName())
                    .addValue("email", user.getEmail())
                    .addValue("phone", user.getPhone())
                    .addValue("passwordHash", user.getPasswordHash())
                    .addValue("role", user.getRole())
                    .addValue("isActive", user.isActive());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE users SET full_name = :fullName, phone = :phone, is_active = :isActive, " +
                    "role = :role, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("fullName", user.getFullName())
                    .addValue("phone", user.getPhone())
                    .addValue("isActive", user.isActive())
                    .addValue("role", user.getRole())
                    .addValue("id", user.getId());
            jdbcTemplate.update(sql, params);
        }
        return user;
    }

    public void updatePassword(Long userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = :passwordHash, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("passwordHash", newPasswordHash)
                .addValue("id", userId);
        jdbcTemplate.update(sql, params);
    }

    public void updateStatus(Long userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = :isActive, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("isActive", isActive)
                .addValue("id", userId);
        jdbcTemplate.update(sql, params);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id DESC";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }

    public List<User> searchUsers(String query) {
        String sql = "SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(:q) OR LOWER(email) LIKE LOWER(:q) OR phone LIKE :q ORDER BY id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource("q", "%" + query.trim() + "%");
        return jdbcTemplate.query(sql, params, USER_ROW_MAPPER);
    }

    public long countUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ROLE_USER'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }
}
