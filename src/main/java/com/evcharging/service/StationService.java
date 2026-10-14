package com.evcharging.service;

import com.evcharging.dto.StationFormDto;
import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.ChargerRepository;
import com.evcharging.repository.StationRepository;
import com.evcharging.util.GeoLocationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final ChargerRepository chargerRepository;

    public StationService(StationRepository stationRepository, ChargerRepository chargerRepository) {
        this.stationRepository = stationRepository;
        this.chargerRepository = chargerRepository;
    }

    public List<Station> searchStations(StationSearchCriteria criteria) {
        List<Station> stations = stationRepository.search(criteria);

        // If user coordinates provided, compute distance and filter/sort
        if (criteria.getUserLat() != null && criteria.getUserLng() != null) {
            for (Station s : stations) {
                double distance = GeoLocationUtils.calculateDistanceKm(
                        criteria.getUserLat(), criteria.getUserLng(),
                        s.getLatitude().doubleValue(), s.getLongitude().doubleValue());
                s.setDistanceKm(distance);
            }

            // Filter by radius if specified
            if (criteria.getRadiusKm() != null && criteria.getRadiusKm() > 0) {
                stations = new ArrayList<>(stations.stream()
                        .filter(s -> s.getDistanceKm() <= criteria.getRadiusKm())
                        .toList());
            }

            // Sort ascending by distance
            stations.sort(Comparator.comparing(Station::getDistanceKm));
        }

        // Attach chargers for each station
        for (Station s : stations) {
            s.setChargers(chargerRepository.findByStationId(s.getId()));
        }

        return stations;
    }

    public Optional<Station> getStationDetails(Long id) {
        Optional<Station> stationOpt = stationRepository.findById(id);
        stationOpt.ifPresent(s -> s.setChargers(chargerRepository.findByStationId(s.getId())));
        return stationOpt;
    }

    public List<Station> getAllStations(boolean onlyActive) {
        List<Station> stations = stationRepository.findAll(onlyActive);
        for (Station s : stations) {
            s.setChargers(chargerRepository.findByStationId(s.getId()));
        }
        return stations;
    }

    public List<String> getAllCities() {
        return stationRepository.findAllCities();
    }

    @Transactional
    public Station saveOrUpdateStation(StationFormDto dto) {
        Station s;
        if (dto.getId() != null) {
            s = stationRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + dto.getId()));
        } else {
            s = new Station();
        }

        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setAddress(dto.getAddress());
        s.setArea(dto.getArea());
        s.setCity(dto.getCity());
        s.setState(dto.getState());
        s.setPostalCode(dto.getPostalCode());
        s.setLatitude(dto.getLatitude());
        s.setLongitude(dto.getLongitude());
        s.setContactNumber(dto.getContactNumber());
        s.setOpeningTime(dto.getOpeningTime());
        s.setClosingTime(dto.getClosingTime());
        s.setOperatingDays(dto.getOperatingDays());
        s.setParkingInformation(dto.getParkingInformation());
        s.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        s.setDemo(dto.isDemo());

        Station saved = stationRepository.save(s);

        // Parse and save amenities
        if (dto.getAmenitiesCsv() != null) {
            String[] split = dto.getAmenitiesCsv().split(",");
            List<String> amenities = new ArrayList<>();
            for (String a : split) {
                if (!a.trim().isBlank()) {
                    amenities.add(a.trim());
                }
            }
            stationRepository.saveAmenities(saved.getId(), amenities);
        }

        return saved;
    }

    @Transactional
    public void toggleStationStatus(Long id, String status) {
        stationRepository.updateStatus(id, status);
    }

    public long countTotalStations() {
        return stationRepository.countTotalStations();
    }

    public long countActiveStations() {
        return stationRepository.countActiveStations();
    }
}
