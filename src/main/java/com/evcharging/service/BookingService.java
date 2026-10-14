package com.evcharging.service;

import com.evcharging.dto.BookingRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.BookingStatusHistory;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.exception.SlotUnavailableException;
import com.evcharging.exception.UnauthorizedAccessException;
import com.evcharging.repository.BookingRepository;
import com.evcharging.repository.ChargerRepository;
import com.evcharging.repository.StationRepository;
import com.evcharging.util.ReferenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final StationRepository stationRepository;
    private final ChargerRepository chargerRepository;

    public BookingService(BookingRepository bookingRepository, StationRepository stationRepository, ChargerRepository chargerRepository) {
        this.bookingRepository = bookingRepository;
        this.stationRepository = stationRepository;
        this.chargerRepository = chargerRepository;
    }

    /**
     * Atomically creates a booking while acquiring a database lock on the charger to prevent race conditions.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Booking createBooking(Long userId, BookingRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book for past dates");
        }

        if (request.getBookingDate().isEqual(LocalDate.now()) && request.getStartTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Cannot book a past time slot for today");
        }

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + request.getStationId()));

        if (!"ACTIVE".equalsIgnoreCase(station.getStatus())) {
            throw new IllegalStateException("Selected charging station is currently " + station.getStatus());
        }

        Charger charger = chargerRepository.findById(request.getChargerId())
                .orElseThrow(() -> new ResourceNotFoundException("Charger not found: " + request.getChargerId()));

        if (!"OPERATIONAL".equalsIgnoreCase(charger.getOperationalStatus())) {
            throw new IllegalStateException("Selected charger is currently not operational (" + charger.getOperationalStatus() + ")");
        }

        // Lock charger row to ensure serializable check
        try {
            bookingRepository.lockChargerForUpdate(charger.getId());
        } catch (Exception e) {
            logger.warn("Could not obtain exclusive row lock, proceeding with transactional query check", e);
        }

        // Concurrency check: Ensure no overlapping active bookings
        boolean hasOverlap = bookingRepository.hasOverlappingBooking(
                charger.getId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime(),
                null);

        if (hasOverlap) {
            throw new SlotUnavailableException("The selected charging slot (" + request.getStartTime() + " - " +
                    request.getEndTime() + ") is already booked. Please choose an alternate slot or charger.");
        }

        // Compute duration
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        if (minutes <= 0) minutes = 60;

        // Estimated energy: Power in kW * Hours * 0.85 typical EV charging factor
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        BigDecimal estimatedKwh = charger.getPowerKw().multiply(hours).multiply(BigDecimal.valueOf(0.85)).setScale(2, RoundingMode.HALF_UP);
        if (estimatedKwh.compareTo(BigDecimal.ZERO) <= 0) {
            estimatedKwh = BigDecimal.valueOf(10.00);
        }

        // Estimated Amount: energy * price per kWh
        BigDecimal estimatedAmount = estimatedKwh.multiply(charger.getPricePerKwh()).setScale(2, RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setBookingReference(ReferenceGenerator.generateBookingReference());
        booking.setUserId(userId);
        booking.setStationId(station.getId());
        booking.setChargerId(charger.getId());
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setDurationMinutes((int) minutes);
        booking.setEstimatedEnergyKwh(estimatedKwh);
        booking.setEstimatedAmount(estimatedAmount);
        booking.setBookingStatus("PENDING_PAYMENT");
        booking.setPaymentStatus("PENDING");

        Booking saved = bookingRepository.save(booking);

        // Record status history
        bookingRepository.recordStatusHistory(new BookingStatusHistory(
                saved.getId(), null, "PENDING_PAYMENT", userId, "Booking created; awaiting payment confirmation"
        ));

        logger.info("Successfully created booking {} for user {}", saved.getBookingReference(), userId);
        return saved;
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByReference(String reference) {
        return bookingRepository.findByReference(reference);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> searchBookings(String query, String status, LocalDate date) {
        return bookingRepository.searchBookings(query, status, date);
    }

    public Booking getBookingForUserOrAdmin(Long bookingId, Long currentUserId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        if (!isAdmin && !booking.getUserId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this booking record.");
        }
        return booking;
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long currentUserId, boolean isAdmin, String reason) {
        Booking booking = getBookingForUserOrAdmin(bookingId, currentUserId, isAdmin);

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if ("COMPLETED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalStateException("Cannot cancel a completed booking session");
        }

        // Apply refund if payment was already made
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            booking.setPaymentStatus("REFUNDED");
        }

        String prevStatus = booking.getBookingStatus();
        booking.setBookingStatus("CANCELLED");
        booking.setCancellationReason(reason != null && !reason.isBlank() ? reason : "Cancelled by user");
        booking.setCancelledAt(LocalDateTime.now());

        bookingRepository.save(booking);

        bookingRepository.recordStatusHistory(new BookingStatusHistory(
                booking.getId(), prevStatus, "CANCELLED", currentUserId,
                "Cancelled: " + booking.getCancellationReason() + (booking.getPaymentStatus().equals("REFUNDED") ? " (Refund initiated)" : "")
        ));

        logger.info("Cancelled booking reference: {} by user: {}", booking.getBookingReference(), currentUserId);
    }

    /**
     * Automatic job running every 5 minutes to release expired unpaid reservations.
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupStaleBookings() {
        int expired = bookingRepository.expireStalePendingBookings(15);
        if (expired > 0) {
            logger.info("System auto-cleanup released {} expired unpaid bookings", expired);
        }
    }

    public long countTotalBookings() {
        return bookingRepository.countTotalBookings();
    }

    public long countConfirmedBookings() {
        return bookingRepository.countConfirmedBookings();
    }

    public long countUserBookings(Long userId) {
        return bookingRepository.countBookingsByUserId(userId);
    }

    public long countUserUpcomingBookings(Long userId) {
        return bookingRepository.countUpcomingBookingsByUserId(userId);
    }
}
