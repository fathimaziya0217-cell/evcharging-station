package com.evcharging;

import com.evcharging.dto.StationFormDto;
import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Station;
import com.evcharging.service.StationService;
import com.evcharging.util.GeoLocationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("Should save a new charging station and search by city and area")
    void testSaveAndSearchStation() {
        StationFormDto dto = new StationFormDto();
        dto.setName("Kannur Highway FastHub");
        dto.setDescription("High speed test charging facility");
        dto.setAddress("NH 66 Bypass");
        dto.setArea("Mele Chovva");
        dto.setCity("Kannur");
        dto.setState("Kerala");
        dto.setPostalCode("670006");
        dto.setLatitude(new BigDecimal("11.881234"));
        dto.setLongitude(new BigDecimal("75.378901"));
        dto.setContactNumber("+914972700000");
        dto.setOpeningTime(LocalTime.of(6, 0));
        dto.setClosingTime(LocalTime.of(23, 0));
        dto.setOperatingDays("All Days");
        dto.setStatus("ACTIVE");
        dto.setDemo(true);
        dto.setAmenitiesCsv("Restroom, Free Wi-Fi, Café");

        Station saved = stationService.saveOrUpdateStation(dto);
        assertNotNull(saved.getId());

        // Search by city
        StationSearchCriteria criteria = new StationSearchCriteria();
        criteria.setCity("Kannur");
        List<Station> results = stationService.searchStations(criteria);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getName().equals("Kannur Highway FastHub")));

        // Search by query
        criteria = new StationSearchCriteria();
        criteria.setQuery("Chovva");
        results = stationService.searchStations(criteria);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("Should accurately calculate distance using Haversine formula")
    void testGeoLocationDistanceCalculation() {
        // Kannur Caltex: 11.874477, 75.370369
        // Thalassery Bus Stand: 11.751230, 75.492540
        // Expected distance: approx 19-20 km
        double distance = GeoLocationUtils.calculateDistanceKm(11.874477, 75.370369, 11.751230, 75.492540);
        assertTrue(distance > 18.0 && distance < 22.0, "Distance should be ~19.5 km between Kannur and Thalassery, but was: " + distance);
    }
}
