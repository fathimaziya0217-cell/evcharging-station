package com.evcharging;

import com.evcharging.dto.BookingRequest;
import com.evcharging.dto.ChargerFormDto;
import com.evcharging.dto.PaymentRequest;
import com.evcharging.dto.RegisterRequest;
import com.evcharging.dto.StationFormDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Payment;
import com.evcharging.entity.Station;
import com.evcharging.entity.User;
import com.evcharging.exception.UnauthorizedAccessException;
import com.evcharging.service.BookingService;
import com.evcharging.service.ChargerService;
import com.evcharging.service.PaymentService;
import com.evcharging.service.StationService;
import com.evcharging.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private StationService stationService;

    @Autowired
    private ChargerService chargerService;

    private Long testUserId;
    private Long otherUserId;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Create User 1
        RegisterRequest reg1 = new RegisterRequest();
        reg1.setFullName("Payment Tester 1");
        reg1.setEmail("paytest1_" + System.currentTimeMillis() + "@gmail.com");
        reg1.setPhone("+919876540001");
        reg1.setPassword("Pass@123");
        reg1.setConfirmPassword("Pass@123");
        reg1.setRegistrationNumber("KL-13-PA-" + (System.currentTimeMillis() % 10000));
        reg1.setMake("Tata");
        reg1.setModel("Nexon");
        reg1.setVehicleType("4-Wheeler");
        reg1.setPreferredConnector("CCS2");
        User u1 = userService.registerUser(reg1);
        testUserId = u1.getId();

        // Create User 2
        RegisterRequest reg2 = new RegisterRequest();
        reg2.setFullName("Payment Tester 2");
        reg2.setEmail("paytest2_" + System.currentTimeMillis() + "@gmail.com");
        reg2.setPhone("+919876540002");
        reg2.setPassword("Pass@123");
        reg2.setConfirmPassword("Pass@123");
        reg2.setRegistrationNumber("KL-13-PB-" + (System.currentTimeMillis() % 10000));
        reg2.setMake("Ather");
        reg2.setModel("450X");
        reg2.setVehicleType("2-Wheeler");
        reg2.setPreferredConnector("Type 2");
        User u2 = userService.registerUser(reg2);
        otherUserId = u2.getId();

        // Create Station & Charger
        StationFormDto st = new StationFormDto();
        st.setName("Kozhikode PayHub");
        st.setAddress("Mavoor Road");
        st.setArea("Mavoor");
        st.setCity("Kozhikode");
        st.setState("Kerala");
        st.setPostalCode("673004");
        st.setLatitude(new BigDecimal("11.258750"));
        st.setLongitude(new BigDecimal("75.780410"));
        st.setContactNumber("+914952700000");
        st.setStatus("ACTIVE");
        Station station = stationService.saveOrUpdateStation(st);

        ChargerFormDto ch = new ChargerFormDto();
        ch.setStationId(station.getId());
        ch.setChargerIdentifier("PAY-GUN-01");
        ch.setChargerType("DC Fast Charging");
        ch.setConnectorType("CCS2");
        ch.setPowerKw(new BigDecimal("60.00"));
        ch.setPricePerKwh(new BigDecimal("18.50"));
        ch.setAvailabilityStatus("AVAILABLE");
        ch.setOperationalStatus("OPERATIONAL");
        Charger charger = chargerService.saveOrUpdateCharger(ch);

        // Create initial booking
        BookingRequest bReq = new BookingRequest();
        bReq.setStationId(station.getId());
        bReq.setChargerId(charger.getId());
        bReq.setBookingDate(LocalDate.now().plusDays(2));
        bReq.setStartTime(LocalTime.of(11, 0));
        bReq.setEndTime(LocalTime.of(12, 0));
        testBooking = bookingService.createBooking(testUserId, bReq);
    }

    @Test
    @DisplayName("Should simulate SUCCESS payment and transition booking to CONFIRMED and PAID")
    void testDemoPaymentSuccess() {
        PaymentRequest req = new PaymentRequest();
        req.setBookingId(testBooking.getId());
        req.setPaymentMethod("UPI");
        req.setDemoOutcome("SUCCESS");

        Payment payment = paymentService.processPayment(testUserId, req);

        assertNotNull(payment.getId());
        assertEquals("SUCCESS", payment.getPaymentStatus());
        assertNotNull(payment.getPaidAt());

        // Verify booking state
        Booking updated = bookingService.getBookingById(testBooking.getId()).orElseThrow();
        assertEquals("CONFIRMED", updated.getBookingStatus());
        assertEquals("PAID", updated.getPaymentStatus());
    }

    @Test
    @DisplayName("Should simulate FAILED payment, record attempt, and keep booking in pending state")
    void testDemoPaymentFailure() {
        PaymentRequest req = new PaymentRequest();
        req.setBookingId(testBooking.getId());
        req.setPaymentMethod("CARD");
        req.setDemoOutcome("FAILED");

        Payment payment = paymentService.processPayment(testUserId, req);

        assertEquals("FAILED", payment.getPaymentStatus());
        assertNotNull(payment.getFailureReason());

        Booking updated = bookingService.getBookingById(testBooking.getId()).orElseThrow();
        assertEquals("PENDING_PAYMENT", updated.getBookingStatus());
    }

    @Test
    @DisplayName("Should prevent double payment processing for already confirmed booking")
    void testPreventDuplicatePayment() {
        PaymentRequest req = new PaymentRequest();
        req.setBookingId(testBooking.getId());
        req.setPaymentMethod("DEMO");
        req.setDemoOutcome("SUCCESS");
        paymentService.processPayment(testUserId, req);

        // Attempt second payment
        assertThrows(IllegalStateException.class, () -> paymentService.processPayment(testUserId, req));
    }

    @Test
    @DisplayName("Should reject payment attempt from a user who does not own the booking")
    void testUnauthorizedPaymentAttempt() {
        PaymentRequest req = new PaymentRequest();
        req.setBookingId(testBooking.getId());
        req.setPaymentMethod("DEMO");
        req.setDemoOutcome("SUCCESS");

        assertThrows(UnauthorizedAccessException.class, () -> paymentService.processPayment(otherUserId, req));
    }
}
