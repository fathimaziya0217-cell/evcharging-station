package com.evcharging.repository;

import com.evcharging.entity.AdminAuditLog;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuditLogRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AuditLogRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<AdminAuditLog> AUDIT_ROW_MAPPER = new RowMapper<>() {
        @Override
        public AdminAuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            AdminAuditLog log = new AdminAuditLog();
            log.setId(rs.getLong("id"));
            log.setAdminUserId(rs.getLong("admin_user_id"));
            log.setAction(rs.getString("action"));
            log.setEntityType(rs.getString("entity_type"));
            log.setEntityId(rs.getString("entity_id"));
            log.setDetails(rs.getString("details"));
            log.setIpAddress(rs.getString("ip_address"));
            if (rs.getTimestamp("created_at") != null) {
                log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            try {
                log.setAdminName(rs.getString("admin_name"));
            } catch (SQLException ignored) {}
            return log;
        }
    };

    public void logAction(Long adminUserId, String action, String entityType, String entityId, String details, String ipAddress) {
        String sql = "INSERT INTO admin_audit_logs (admin_user_id, action, entity_type, entity_id, details, ip_address, created_at) " +
                "VALUES (:adminId, :action, :type, :id, :details, :ip, NOW())";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("adminId", adminUserId)
                .addValue("action", action)
                .addValue("type", entityType)
                .addValue("id", entityId)
                .addValue("details", details)
                .addValue("ip", ipAddress != null ? ipAddress : "127.0.0.1");
        jdbcTemplate.update(sql, params);
    }

    public List<AdminAuditLog> findRecent(int limit) {
        String sql = "SELECT a.*, u.full_name as admin_name FROM admin_audit_logs a " +
                "INNER JOIN users u ON a.admin_user_id = u.id ORDER BY a.id DESC LIMIT :limit";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("limit", limit), AUDIT_ROW_MAPPER);
    }
}
