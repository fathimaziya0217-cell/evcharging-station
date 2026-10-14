package com.evcharging.controller.api;

import com.evcharging.dto.BookingRequest;
import com.evcharging.entity.Booking;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        Booking booking = bookingService.createBooking(currentUserId, request);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        return ResponseEntity.ok(bookingService.getUserBookings(currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable("id") Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();
        return ResponseEntity.ok(bookingService.getBookingForUserOrAdmin(id, currentUserId, isAdmin));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable("id") Long id,
                                              @RequestParam(value = "reason", required = false) String reason) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();
        bookingService.cancelBooking(id, currentUserId, isAdmin, reason);
        return ResponseEntity.noContent().build();
    }
}
