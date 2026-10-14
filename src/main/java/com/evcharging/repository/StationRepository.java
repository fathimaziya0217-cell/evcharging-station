package com.evcharging.repository;

import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Station;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Station> STATION_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
            Station s = new Station();
            s.setId(rs.getLong("id"));
            s.setName(rs.getString("name"));
            s.setDescription(rs.getString("description"));
            s.setAddress(rs.getString("address"));
            s.setArea(rs.getString("area"));
            s.setCity(rs.getString("city"));
            s.setState(rs.getString("state"));
            s.setPostalCode(rs.getString("postal_code"));
            s.setLatitude(rs.getBigDecimal("latitude"));
            s.setLongitude(rs.getBigDecimal("longitude"));
            s.setContactNumber(rs.getString("contact_number"));
            if (rs.getTime("opening_time") != null) {
                s.setOpeningTime(rs.getTime("opening_time").toLocalTime());
            }
            if (rs.getTime("closing_time") != null) {
                s.setClosingTime(rs.getTime("closing_time").toLocalTime());
            }
            s.setOperatingDays(rs.getString("operating_days"));
            s.setParkingInformation(rs.getString("parking_information"));
            s.setStatus(rs.getString("status"));
            s.setDemo(rs.getBoolean("is_demo"));
            if (rs.getTimestamp("created_at") != null) {
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return s;
        }
    };

    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM stations WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Station station = jdbcTemplate.queryForObject(sql, params, STATION_ROW_MAPPER);
            if (station != null) {
                station.setAmenities(findAmenitiesByStationId(station.getId()));
            }
            return Optional.ofNullable(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Station> findAll(boolean onlyActive) {
        String sql = onlyActive ?
                "SELECT * FROM stations WHERE status = 'ACTIVE' ORDER BY id ASC" :
                "SELECT * FROM stations ORDER BY id DESC";
        List<Station> list = jdbcTemplate.query(sql, STATION_ROW_MAPPER);
        for (Station s : list) {
            s.setAmenities(findAmenitiesByStationId(s.getId()));
        }
        return list;
    }

    public List<Station> search(StationSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT s.* FROM stations s ");
        boolean joinsChargers = criteria.getChargerType() != null && !criteria.getChargerType().isBlank()
                || criteria.getConnectorType() != null && !criteria.getConnectorType().isBlank()
                || criteria.getMinPowerKw() != null
                || criteria.getMaxPricePerKwh() != null
                || (criteria.getAvailabilityStatus() != null && !criteria.getAvailabilityStatus().isBlank());

        if (joinsChargers) {
            sql.append("INNER JOIN chargers c ON s.id = c.station_id ");
        }

        sql.append("WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        // Admin criteria vs public criteria
        if (criteria.getAvailabilityStatus() != null && criteria.getAvailabilityStatus().equalsIgnoreCase("ALL_STATUSES")) {
            // Include non-active stations if specifically requested
        } else {
            sql.append("AND s.status = 'ACTIVE' ");
        }

        if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
            sql.append("AND LOWER(s.city) = LOWER(:city) ");
            params.addValue("city", criteria.getCity().trim());
        }

        if (criteria.getArea() != null && !criteria.getArea().isBlank()) {
            sql.append("AND LOWER(s.area) LIKE LOWER(:area) ");
            params.addValue("area", "%" + criteria.getArea().trim() + "%");
        }

        if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
            sql.append("AND (LOWER(s.name) LIKE LOWER(:q) OR LOWER(s.address) LIKE LOWER(:q) OR LOWER(s.area) LIKE LOWER(:q) OR LOWER(s.city) LIKE LOWER(:q)) ");
            params.addValue("q", "%" + criteria.getQuery().trim() + "%");
        }

        if (criteria.getChargerType() != null && !criteria.getChargerType().isBlank()) {
            sql.append("AND c.charger_type = :chargerType ");
            params.addValue("chargerType", criteria.getChargerType().trim());
        }

        if (criteria.getConnectorType() != null && !criteria.getConnectorType().isBlank()) {
            sql.append("AND c.connector_type = :connectorType ");
            params.addValue("connectorType", criteria.getConnectorType().trim());
        }

        if (criteria.getMinPowerKw() != null) {
            sql.append("AND c.power_kw >= :minPower ");
            params.addValue("minPower", criteria.getMinPowerKw());
        }

        if (criteria.getMaxPricePerKwh() != null) {
            sql.append("AND c.price_per_kwh <= :maxPrice ");
            params.addValue("maxPrice", criteria.getMaxPricePerKwh());
        }

        if (criteria.getAvailabilityStatus() != null && !criteria.getAvailabilityStatus().isBlank() && !criteria.getAvailabilityStatus().equalsIgnoreCase("ALL_STATUSES")) {
            sql.append("AND c.availability_status = :availStatus ");
            params.addValue("availStatus", criteria.getAvailabilityStatus().trim());
        }

        sql.append("ORDER BY s.id ASC");

        List<Station> stations = jdbcTemplate.query(sql.toString(), params, STATION_ROW_MAPPER);
        for (Station s : stations) {
            s.setAmenities(findAmenitiesByStationId(s.getId()));
        }
        return stations;
    }

    public Station save(Station s) {
        if (s.getId() == null) {
            String sql = "INSERT INTO stations (name, description, address, area, city, state, postal_code, " +
                    "latitude, longitude, contact_number, opening_time, closing_time, operating_days, " +
                    "parking_information, status, is_demo, created_at, updated_at) " +
                    "VALUES (:name, :description, :address, :area, :city, :state, :postalCode, " +
                    ":latitude, :longitude, :contactNumber, :openingTime, :closingTime, :operatingDays, " +
                    ":parkingInfo, :status, :isDemo, NOW(), NOW())";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", s.getName().trim())
                    .addValue("description", s.getDescription())
                    .addValue("address", s.getAddress().trim())
                    .addValue("area", s.getArea().trim())
                    .addValue("city", s.getCity().trim())
                    .addValue("state", s.getState().trim())
                    .addValue("postalCode", s.getPostalCode().trim())
                    .addValue("latitude", s.getLatitude())
                    .addValue("longitude", s.getLongitude())
                    .addValue("contactNumber", s.getContactNumber().trim())
                    .addValue("openingTime", s.getOpeningTime())
                    .addValue("closingTime", s.getClosingTime())
                    .addValue("operatingDays", s.getOperatingDays())
                    .addValue("parkingInfo", s.getParkingInformation())
                    .addValue("status", s.getStatus())
                    .addValue("isDemo", s.isDemo());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
            if (keyHolder.getKey() != null) {
                s.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE stations SET name = :name, description = :description, address = :address, " +
                    "area = :area, city = :city, state = :state, postal_code = :postalCode, latitude = :latitude, " +
                    "longitude = :longitude, contact_number = :contactNumber, opening_time = :openingTime, " +
                    "closing_time = :closingTime, operating_days = :operatingDays, parking_information = :parkingInfo, " +
                    "status = :status, updated_at = NOW() WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", s.getName().trim())
                    .addValue("description", s.getDescription())
                    .addValue("address", s.getAddress().trim())
                    .addValue("area", s.getArea().trim())
                    .addValue("city", s.getCity().trim())
                    .addValue("state", s.getState().trim())
                    .addValue("postalCode", s.getPostalCode().trim())
                    .addValue("latitude", s.getLatitude())
                    .addValue("longitude", s.getLongitude())
                    .addValue("contactNumber", s.getContactNumber().trim())
                    .addValue("openingTime", s.getOpeningTime())
                    .addValue("closingTime", s.getClosingTime())
                    .addValue("operatingDays", s.getOperatingDays())
                    .addValue("parkingInfo", s.getParkingInformation())
                    .addValue("status", s.getStatus())
                    .addValue("id", s.getId());
            jdbcTemplate.update(sql, params);
        }
        return s;
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE stations SET status = :status, updated_at = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    public List<String> findAmenitiesByStationId(Long stationId) {
        String sql = "SELECT amenity_name FROM station_amenities WHERE station_id = :stationId";
        MapSqlParameterSource params = new MapSqlParameterSource("stationId", stationId);
        return jdbcTemplate.queryForList(sql, params, String.class);
    }

    public void saveAmenities(Long stationId, List<String> amenities) {
        String delSql = "DELETE FROM station_amenities WHERE station_id = :stationId";
        jdbcTemplate.update(delSql, new MapSqlParameterSource("stationId", stationId));

        if (amenities != null && !amenities.isEmpty()) {
            String insSql = "INSERT INTO station_amenities (station_id, amenity_name) VALUES (:stationId, :name)";
            for (String amenity : amenities) {
                if (amenity != null && !amenity.trim().isBlank()) {
                    jdbcTemplate.update(insSql, new MapSqlParameterSource()
                            .addValue("stationId", stationId)
                            .addValue("name", amenity.trim()));
                }
            }
        }
    }

    public List<String> findAllCities() {
        String sql = "SELECT DISTINCT city FROM stations WHERE status = 'ACTIVE' ORDER BY city ASC";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public long countTotalStations() {
        String sql = "SELECT COUNT(*) FROM stations";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }

    public long countActiveStations() {
        String sql = "SELECT COUNT(*) FROM stations WHERE status = 'ACTIVE'";
        Long count = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }
}
