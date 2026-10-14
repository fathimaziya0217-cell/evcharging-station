package com.evcharging;

import com.evcharging.dto.BookingRequest;
import com.evcharging.dto.ChargerFormDto;
import com.evcharging.dto.RegisterRequest;
import com.evcharging.dto.StationFormDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.entity.User;
import com.evcharging.exception.SlotUnavailableException;
import com.evcharging.service.BookingService;
import com.evcharging.service.ChargerService;
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
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private StationService stationService;

    @Autowired
    private ChargerService chargerService;

    private Long testUserId;
    private Long testStationId;
    private Long testChargerId;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() {
        // Create User
        RegisterRequest reg = new RegisterRequest();
        reg.setFullName("Booking Tester");
        reg.setEmail("tester" + System.currentTimeMillis() + "@gmail.com");
        reg.setPhone("+919876543299");
        reg.setPassword("Pass@123");
        reg.setConfirmPassword("Pass@123");
        reg.setRegistrationNumber("KL-13-ZZ-" + (System.currentTimeMillis() % 10000));
        reg.setMake("Tata");
        reg.setModel("Nexon");
        reg.setVehicleType("4-Wheeler");
        reg.setPreferredConnector("CCS2");
        User user = userService.registerUser(reg);
        testUserId = user.getId();

        // Create Station
        StationFormDto st = new StationFormDto();
        st.setName("Test Fast Station");
        st.setAddress("Test Road");
        st.setArea("Taliparamba");
        st.setCity("Kannur");
        st.setState("Kerala");
        st.setPostalCode("670141");
        st.setLatitude(new BigDecimal("12.043740"));
        st.setLongitude(new BigDecimal("75.358290"));
        st.setContactNumber("+914602200000");
        st.setStatus("ACTIVE");
        Station station = stationService.saveOrUpdateStation(st);
        testStationId = station.getId();

        // Create Charger
        ChargerFormDto ch = new ChargerFormDto();
        ch.setStationId(testStationId);
        ch.setChargerIdentifier("GUN-TEST-01");
        ch.setChargerType("DC Fast Charging");
        ch.setConnectorType("CCS2");
        ch.setPowerKw(new BigDecimal("60.00"));
        ch.setPricePerKwh(new BigDecimal("18.00"));
        ch.setAvailabilityStatus("AVAILABLE");
        ch.setOperationalStatus("OPERATIONAL");
        Charger charger = chargerService.saveOrUpdateCharger(ch);
        testChargerId = charger.getId();

        bookingDate = LocalDate.now().plusDays(1);
    }

    @Test
    @DisplayName("Should successfully create a slot booking with generated reference and calculated amount")
    void testCreateBookingSuccess() {
        BookingRequest req = new BookingRequest();
        req.setStationId(testStationId);
        req.setChargerId(testChargerId);
        req.setBookingDate(bookingDate);
        req.setStartTime(LocalTime.of(10, 0));
        req.setEndTime(LocalTime.of(11, 0));

        Booking booking = bookingService.createBooking(testUserId, req);

        assertNotNull(booking.getId());
        assertNotNull(booking.getBookingReference());
        assertTrue(booking.getBookingReference().startsWith("EVC-"));
        assertEquals("PENDING_PAYMENT", booking.getBookingStatus());
        assertEquals("PENDING", booking.getPaymentStatus());
        assertTrue(booking.getEstimatedAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should reject overlapping booking request for the same charger and time slot")
    void testPreventOverlappingBookings() {
        BookingRequest req1 = new BookingRequest();
        req1.setStationId(testStationId);
        req1.setChargerId(testChargerId);
        req1.setBookingDate(bookingDate);
        req1.setStartTime(LocalTime.of(14, 0));
        req1.setEndTime(LocalTime.of(15, 0));
        bookingService.createBooking(testUserId, req1);

        // Attempt 2: Overlapping start time (14:30 - 15:30)
        BookingRequest req2 = new BookingRequest();
        req2.setStationId(testStationId);
        req2.setChargerId(testChargerId);
        req2.setBookingDate(bookingDate);
        req2.setStartTime(LocalTime.of(14, 30));
        req2.setEndTime(LocalTime.of(15, 30));

        assertThrows(SlotUnavailableException.class, () -> bookingService.createBooking(testUserId, req2));
    }

    @Test
    @DisplayName("Should reject booking if start time is after end time")
    void testInvalidTimeRejection() {
        BookingRequest req = new BookingRequest();
        req.setStationId(testStationId);
        req.setChargerId(testChargerId);
        req.setBookingDate(bookingDate);
        req.setStartTime(LocalTime.of(16, 0));
        req.setEndTime(LocalTime.of(15, 0)); // invalid end time

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(testUserId, req));
    }

    @Test
    @DisplayName("Should successfully cancel an eligible booking")
    void testCancelBooking() {
        BookingRequest req = new BookingRequest();
        req.setStationId(testStationId);
        req.setChargerId(testChargerId);
        req.setBookingDate(bookingDate);
        req.setStartTime(LocalTime.of(16, 0));
        req.setEndTime(LocalTime.of(17, 0));

        Booking booking = bookingService.createBooking(testUserId, req);
        bookingService.cancelBooking(booking.getId(), testUserId, false, "Change of travel plan");

        Booking updated = bookingService.getBookingById(booking.getId()).orElseThrow();
        assertEquals("CANCELLED", updated.getBookingStatus());
        assertNotNull(updated.getCancelledAt());
    }
}
