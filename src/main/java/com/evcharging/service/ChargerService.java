package com.evcharging.service;

import com.evcharging.dto.ChargerFormDto;
import com.evcharging.dto.SlotAvailabilityDto;
import com.evcharging.dto.TimeSlotDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.BookingRepository;
import com.evcharging.repository.ChargerRepository;
import com.evcharging.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChargerService {

    private final ChargerRepository chargerRepository;
    private final StationRepository stationRepository;
    private final BookingRepository bookingRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    public ChargerService(ChargerRepository chargerRepository, StationRepository stationRepository, BookingRepository bookingRepository) {
        this.chargerRepository = chargerRepository;
        this.stationRepository = stationRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Charger> getChargersByStation(Long stationId) {
        return chargerRepository.findByStationId(stationId);
    }

    public Optional<Charger> getChargerById(Long id) {
        return chargerRepository.findById(id);
    }

    public List<Charger> getAllChargers() {
        return chargerRepository.findAll();
    }

    @Transactional
    public Charger saveOrUpdateCharger(ChargerFormDto dto) {
        Charger c;
        if (dto.getId() != null) {
            c = chargerRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Charger not found: " + dto.getId()));
        } else {
            c = new Charger();
        }

        c.setStationId(dto.getStationId());
        c.setChargerIdentifier(dto.getChargerIdentifier());
        c.setChargerType(dto.getChargerType());
        c.setConnectorType(dto.getConnectorType());
        c.setPowerKw(dto.getPowerKw());
        c.setPricePerKwh(dto.getPricePerKwh());
        c.setAvailabilityStatus(dto.getAvailabilityStatus() != null ? dto.getAvailabilityStatus() : "AVAILABLE");
        c.setOperationalStatus(dto.getOperationalStatus() != null ? dto.getOperationalStatus() : "OPERATIONAL");
        c.setDescription(dto.getDescription());

        return chargerRepository.save(c);
    }

    @Transactional
    public void updatePricing(Long id, BigDecimal pricePerKwh) {
        chargerRepository.updatePricing(id, pricePerKwh);
    }

    @Transactional
    public void updateOperationalStatus(Long id, String status) {
        chargerRepository.updateOperationalStatus(id, status);
        if ("MAINTENANCE".equalsIgnoreCase(status) || "DECOMMISSIONED".equalsIgnoreCase(status)) {
            chargerRepository.updateAvailability(id, "OFFLINE");
        } else if ("OPERATIONAL".equalsIgnoreCase(status)) {
            chargerRepository.updateAvailability(id, "AVAILABLE");
        }
    }

    /**
     * Calculates available time slots for a specific charger on a selected date.
     * Takes station operating hours and existing active bookings into account.
     */
    public SlotAvailabilityDto getSlotAvailability(Long chargerId, LocalDate date) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new ResourceNotFoundException("Charger not found: " + chargerId));
        Station station = stationRepository.findById(charger.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + charger.getStationId()));

        List<Booking> activeBookings = bookingRepository.findActiveBookingsForChargerAndDate(chargerId, date);

        LocalTime openTime = station.getOpeningTime() != null ? station.getOpeningTime() : LocalTime.of(6, 0);
        LocalTime closeTime = station.getClosingTime() != null ? station.getClosingTime() : LocalTime.of(23, 0);

        List<TimeSlotDto> slotList = new ArrayList<>();
        LocalTime current = openTime;
        LocalTime nowTime = LocalTime.now();
        boolean isToday = date.isEqual(LocalDate.now());

        // Generate 60-minute time intervals
        while (current.plusMinutes(60).isBefore(closeTime) || current.plusMinutes(60).equals(closeTime)) {
            LocalTime slotStart = current;
            LocalTime slotEnd = current.plusMinutes(60);
            String label = slotStart.format(TIME_FMT) + " - " + slotEnd.format(TIME_FMT);

            boolean isAvailable = true;
            String reason = "Available";

            // If operational status is not OPERATIONAL, mark offline
            if (!"OPERATIONAL".equalsIgnoreCase(charger.getOperationalStatus())) {
                isAvailable = false;
                reason = "Charger under " + charger.getOperationalStatus();
            } else if (isToday && slotStart.isBefore(nowTime)) {
                // Past slot for today
                isAvailable = false;
                reason = "Past time";
            } else {
                // Check collision with any existing active booking
                for (Booking b : activeBookings) {
                    if (slotStart.isBefore(b.getEndTime()) && slotEnd.isAfter(b.getStartTime())) {
                        isAvailable = false;
                        reason = "Already Booked";
                        break;
                    }
                }
            }

            slotList.add(new TimeSlotDto(slotStart, slotEnd, label, isAvailable, reason));
            current = current.plusMinutes(60);
        }

        SlotAvailabilityDto dto = new SlotAvailabilityDto();
        dto.setChargerId(charger.getId());
        dto.setChargerIdentifier(charger.getChargerIdentifier());
        dto.setStationId(station.getId());
        dto.setStationName(station.getName());
        dto.setDate(date);
        dto.setSlots(slotList);

        return dto;
    }

    public long countTotalChargers() {
        return chargerRepository.countTotalChargers();
    }

    public long countAvailableChargers() {
        return chargerRepository.countAvailableChargers();
    }
}
