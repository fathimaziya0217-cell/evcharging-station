package com.evcharging.service;

import com.evcharging.dto.PaymentRequest;
import com.evcharging.entity.Booking;
import com.evcharging.entity.BookingStatusHistory;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Payment;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.exception.UnauthorizedAccessException;
import com.evcharging.repository.BookingRepository;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.repository.PaymentRepository;
import com.evcharging.service.payment.PaymentAdapterFactory;
import com.evcharging.service.payment.PaymentGatewayAdapter;
import com.evcharging.util.ReferenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ChargingSessionRepository sessionRepository;
    private final PaymentAdapterFactory adapterFactory;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          ChargingSessionRepository sessionRepository,
                          PaymentAdapterFactory adapterFactory) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.sessionRepository = sessionRepository;
        this.adapterFactory = adapterFactory;
    }

    /**
     * Processes payment for a booking using the configured adapter (Demo simulation or real gateway).
     */
    @Transactional
    public Payment processPayment(Long userId, PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        if (!booking.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("Unauthorized: You cannot pay for another user's booking");
        }

        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new IllegalStateException("This booking has already been paid and confirmed.");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus()) || "EXPIRED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalStateException("Cannot pay for a " + booking.getBookingStatus() + " booking.");
        }

        PaymentGatewayAdapter adapter = adapterFactory.getAdapter();
        Payment payment = adapter.initiatePayment(booking, request);
        Payment savedPayment = paymentRepository.save(payment);

        // Process result via adapter
        savedPayment = adapter.processPaymentResult(savedPayment, request);
        paymentRepository.save(savedPayment);

        if ("SUCCESS".equalsIgnoreCase(savedPayment.getPaymentStatus())) {
            booking.setBookingStatus("CONFIRMED");
            booking.setPaymentStatus("PAID");
            bookingRepository.save(booking);

            bookingRepository.recordStatusHistory(new BookingStatusHistory(
                    booking.getId(), "PENDING_PAYMENT", "CONFIRMED", userId,
                    "Payment confirmed via " + adapter.getProviderName() + " (Ref: " + savedPayment.getPaymentReference() + ")"
            ));

            // Create scheduled charging session if not already existing
            if (sessionRepository.findByBookingId(booking.getId()).isEmpty()) {
                ChargingSession session = new ChargingSession();
                session.setSessionReference(ReferenceGenerator.generateSessionReference());
                session.setBookingId(booking.getId());
                session.setUserId(userId);
                session.setChargerId(booking.getChargerId());
                session.setSessionStatus("SCHEDULED");
                session.setEnergyConsumedKwh(BigDecimal.ZERO);
                session.setInitialSocPercent(20);
                session.setFinalSocPercent(80);
                session.setFinalAmount(booking.getEstimatedAmount());
                session.setNotes("Scheduled charging session for booking " + booking.getBookingReference());
                session.setSimulated(adapterFactory.isDemoMode());
                sessionRepository.save(session);
            }

            logger.info("Payment SUCCESS: Booking {} is now CONFIRMED", booking.getBookingReference());
        } else {
            booking.setPaymentStatus("FAILED");
            bookingRepository.save(booking);

            bookingRepository.recordStatusHistory(new BookingStatusHistory(
                    booking.getId(), "PENDING_PAYMENT", "PENDING_PAYMENT", userId,
                    "Payment failed/cancelled via " + adapter.getProviderName() + " (Reason: " + savedPayment.getFailureReason() + ")"
            ));

            logger.warn("Payment FAILED for booking {}: {}", booking.getBookingReference(), savedPayment.getFailureReason());
        }

        return savedPayment;
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByReference(String reference) {
        return paymentRepository.findByReference(reference);
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> searchPayments(String query, String status) {
        return paymentRepository.searchPayments(query, status);
    }

    public Payment getPaymentForUserOrAdmin(Long paymentId, Long currentUserId, boolean isAdmin) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        if (!isAdmin && !payment.getUserId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to view this payment record.");
        }
        return payment;
    }

    public long countSuccessfulPayments() {
        return paymentRepository.countSuccessfulPayments();
    }

    public BigDecimal getTotalSuccessfulRevenue() {
        return paymentRepository.getTotalSuccessfulRevenue();
    }

    public BigDecimal getUserTotalSpent(Long userId) {
        return paymentRepository.getTotalSpentByUserId(userId);
    }
}
