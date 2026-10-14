package com.evcharging.service;

import com.evcharging.dto.SessionSimulateDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.BookingStatusHistory;
import com.evcharging.entity.Charger;
import com.evcharging.entity.ChargingSession;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.exception.UnauthorizedAccessException;
import com.evcharging.repository.BookingRepository;
import com.evcharging.repository.ChargerRepository;
import com.evcharging.repository.ChargingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChargingSessionService {

    private final ChargingSessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final ChargerRepository chargerRepository;

    public ChargingSessionService(ChargingSessionRepository sessionRepository,
                                  BookingRepository bookingRepository,
                                  ChargerRepository chargerRepository) {
        this.sessionRepository = sessionRepository;
        this.bookingRepository = bookingRepository;
        this.chargerRepository = chargerRepository;
    }

    public Optional<ChargingSession> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    public Optional<ChargingSession> getSessionByBookingId(Long bookingId) {
        return sessionRepository.findByBookingId(bookingId);
    }

    public List<ChargingSession> getUserSessions(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public List<ChargingSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public ChargingSession getSessionForUserOrAdmin(Long sessionId, Long currentUserId, boolean isAdmin) {
        ChargingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found: " + sessionId));
        if (!isAdmin && !session.getUserId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to view this charging session.");
        }
        return session;
    }

    @Transactional
    public void startSession(Long sessionId, Long adminUserId) {
        ChargingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found: " + sessionId));

        session.setSessionStatus("IN_PROGRESS");
        session.setStartTime(LocalDateTime.now());
        session.setNotes("Charging session started by admin simulation");
        sessionRepository.save(session);

        // Update charger status to OCCUPIED
        chargerRepository.updateAvailability(session.getChargerId(), "OCCUPIED");
    }

    @Transactional
    public void completeSession(SessionSimulateDto dto, Long adminUserId) {
        ChargingSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found: " + dto.getSessionId()));

        Charger charger = chargerRepository.findById(session.getChargerId())
                .orElseThrow(() -> new ResourceNotFoundException("Charger not found: " + session.getChargerId()));

        session.setSessionStatus("COMPLETED");
        if (session.getStartTime() == null) {
            session.setStartTime(LocalDateTime.now().minusMinutes(45));
        }
        session.setEndTime(LocalDateTime.now());
        session.setEnergyConsumedKwh(dto.getEnergyConsumedKwh());
        session.setInitialSocPercent(dto.getInitialSocPercent());
        session.setFinalSocPercent(dto.getFinalSocPercent());

        // Final amount: consumed kWh * charger price per kWh
        BigDecimal finalAmt = dto.getEnergyConsumedKwh().multiply(charger.getPricePerKwh()).setScale(2, RoundingMode.HALF_UP);
        session.setFinalAmount(finalAmt);

        if (dto.getNotes() != null && !dto.getNotes().isBlank()) {
            session.setNotes(dto.getNotes());
        } else {
            session.setNotes("Charging session completed successfully. Total energy: " + dto.getEnergyConsumedKwh() + " kWh");
        }

        sessionRepository.save(session);

        // Release charger back to AVAILABLE
        chargerRepository.updateAvailability(session.getChargerId(), "AVAILABLE");

        // Update associated booking to COMPLETED
        Booking booking = bookingRepository.findById(session.getBookingId()).orElse(null);
        if (booking != null) {
            String prevStatus = booking.getBookingStatus();
            booking.setBookingStatus("COMPLETED");
            bookingRepository.save(booking);

            bookingRepository.recordStatusHistory(new BookingStatusHistory(
                    booking.getId(), prevStatus, "COMPLETED", adminUserId,
                    "Session successfully completed. Consumed: " + dto.getEnergyConsumedKwh() + " kWh"
            ));
        }
    }

    public long countCompletedSessions() {
        return sessionRepository.countCompletedSessions();
    }
}
