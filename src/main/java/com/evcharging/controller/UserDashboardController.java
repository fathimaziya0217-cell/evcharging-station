package com.evcharging.controller;

import com.evcharging.dto.UserProfileDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Payment;
import com.evcharging.entity.Station;
import com.evcharging.entity.UserVehicle;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final UserService userService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final StationService stationService;
    private final ChargingSessionService sessionService;

    public UserDashboardController(UserService userService,
                                   BookingService bookingService,
                                   PaymentService paymentService,
                                   StationService stationService,
                                   ChargingSessionService sessionService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.stationService = stationService;
        this.sessionService = sessionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));

        UserProfileDto profile = userService.getProfile(currentUserId);
        List<Booking> userBookings = bookingService.getUserBookings(currentUserId);
        List<Station> activeStations = stationService.getAllStations(true);
        List<Payment> recentPayments = paymentService.getUserPayments(currentUserId);

        long upcomingCount = bookingService.countUserUpcomingBookings(currentUserId);
        long completedCount = userBookings.stream().filter(b -> "COMPLETED".equalsIgnoreCase(b.getBookingStatus())).count();

        model.addAttribute("user", profile);
        model.addAttribute("totalBookings", userBookings.size());
        model.addAttribute("upcomingBookings", upcomingCount);
        model.addAttribute("completedSessions", completedCount);
        model.addAttribute("totalSpent", paymentService.getUserTotalSpent(currentUserId));
        model.addAttribute("recentBookings", userBookings.stream().limit(5).toList());
        model.addAttribute("availableStations", activeStations.stream().limit(4).toList());
        model.addAttribute("recentPayments", recentPayments.stream().limit(3).toList());

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        UserProfileDto profile = userService.getProfile(currentUserId);
        model.addAttribute("user", profile);
        model.addAttribute("newVehicle", new UserVehicle());
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        try {
            userService.updateProfile(currentUserId, fullName, phone);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/vehicle/add")
    public String addVehicle(@ModelAttribute("newVehicle") UserVehicle vehicle,
                             RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        try {
            userService.addVehicle(currentUserId, vehicle);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding vehicle: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long vehicleId,
                                RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));
        try {
            userService.removeVehicle(currentUserId, vehicleId);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting vehicle: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/my-bookings")
    public String myBookings(@RequestParam(value = "filter", required = false, defaultValue = "ALL") String filter,
                             Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));

        List<Booking> allBookings = bookingService.getUserBookings(currentUserId);
        List<Booking> filtered;

        switch (filter.toUpperCase()) {
            case "UPCOMING" -> filtered = allBookings.stream()
                    .filter(b -> ("CONFIRMED".equalsIgnoreCase(b.getBookingStatus()) || "PENDING_PAYMENT".equalsIgnoreCase(b.getBookingStatus())))
                    .toList();
            case "PENDING" -> filtered = allBookings.stream()
                    .filter(b -> "PENDING_PAYMENT".equalsIgnoreCase(b.getBookingStatus()))
                    .toList();
            case "COMPLETED" -> filtered = allBookings.stream()
                    .filter(b -> "COMPLETED".equalsIgnoreCase(b.getBookingStatus()))
                    .toList();
            case "CANCELLED" -> filtered = allBookings.stream()
                    .filter(b -> "CANCELLED".equalsIgnoreCase(b.getBookingStatus()))
                    .toList();
            default -> filtered = allBookings;
        }

        model.addAttribute("bookings", filtered);
        model.addAttribute("currentFilter", filter.toUpperCase());
        model.addAttribute("totalCount", allBookings.size());
        return "user/my-bookings";
    }

    @GetMapping("/payment-history")
    public String paymentHistory(Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));

        List<Payment> payments = paymentService.getUserPayments(currentUserId);
        model.addAttribute("payments", payments);
        model.addAttribute("totalSpent", paymentService.getUserTotalSpent(currentUserId));
        return "user/payment-history";
    }

    @GetMapping("/session-details/{id}")
    public String sessionDetails(@PathVariable("id") Long id, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();

        ChargingSession session = sessionService.getSessionForUserOrAdmin(id, currentUserId, isAdmin);
        Booking booking = bookingService.getBookingById(session.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for session"));

        model.addAttribute("session", session);
        model.addAttribute("booking", booking);
        return "user/session-details";
    }
}
