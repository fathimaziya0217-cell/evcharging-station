package com.evcharging.controller.api;

import com.evcharging.dto.SlotAvailabilityDto;
import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.service.ChargerService;
import com.evcharging.service.StationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationApiController {

    private final StationService stationService;
    private final ChargerService chargerService;

    public StationApiController(StationService stationService, ChargerService chargerService) {
        this.stationService = stationService;
        this.chargerService = chargerService;
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllActiveStations() {
        return ResponseEntity.ok(stationService.getAllStations(true));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Station>> searchStations(StationSearchCriteria criteria) {
        return ResponseEntity.ok(stationService.searchStations(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationDetails(@PathVariable("id") Long id) {
        return stationService.getStationDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/chargers")
    public ResponseEntity<List<Charger>> getStationChargers(@PathVariable("id") Long stationId) {
        return ResponseEntity.ok(chargerService.getChargersByStation(stationId));
    }

    @GetMapping("/chargers/{chargerId}/availability")
    public ResponseEntity<SlotAvailabilityDto> getAvailability(
            @PathVariable("chargerId") Long chargerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(chargerService.getSlotAvailability(chargerId, date));
    }
}
