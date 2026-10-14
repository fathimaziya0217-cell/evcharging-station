package com.evcharging.controller;

import com.evcharging.dto.BookingRequest;
import com.evcharging.dto.SlotAvailabilityDto;
import com.evcharging.entity.Booking;
import com.evcharging.entity.Charger;
import com.evcharging.entity.Station;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.BookingService;
import com.evcharging.service.ChargerService;
import com.evcharging.service.StationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evcharging.dto.TimeSlotDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingViewController {

    private final BookingService bookingService;
    private final StationService stationService;
    private final ChargerService chargerService;

    public BookingViewController(BookingService bookingService, StationService stationService, ChargerService chargerService) {
        this.bookingService = bookingService;
        this.stationService = stationService;
        this.chargerService = chargerService;
    }

    @GetMapping("/new")
    public String newBookingForm(@RequestParam(value = "stationId", required = false) Long stationId,
                                 @RequestParam(value = "chargerId", required = false) Long chargerId,
                                 @RequestParam(value = "date", required = false) LocalDate date,
                                 Model model) {
        LocalDate bookingDate = (date != null) ? date : LocalDate.now();
        Long effectiveStationId = stationId;

        // 1. If stationId is missing but chargerId is provided
        if (effectiveStationId == null && chargerId != null) {
            Charger ch = chargerService.getChargerById(chargerId).orElse(null);
            if (ch != null) {
                effectiveStationId = ch.getStationId();
            }
        }

        // 2. If both are missing, fallback to first available station
        if (effectiveStationId == null) {
            List<Station> allStations = stationService.getAllStations(true);
            if (!allStations.isEmpty()) {
                effectiveStationId = allStations.get(0).getId();
            }
        }

        Station station = null;
        if (effectiveStationId != null) {
            final Long lookupStationId = effectiveStationId;
            station = stationService.getStationDetails(lookupStationId).orElse(null);
            if (station != null) {
                model.addAttribute("station", station);
                model.addAttribute("chargers", station.getChargers());

                // 3. If chargerId is missing, pick the first operational charger
                if (chargerId == null && station.getChargers() != null && !station.getChargers().isEmpty()) {
                    chargerId = station.getChargers().get(0).getId();
                }
            }
        }

        Charger selectedCharger = null;
        SlotAvailabilityDto availability = null;
        LocalTime defaultStartTime = null;
        LocalTime defaultEndTime = null;

        if (chargerId != null) {
            selectedCharger = chargerService.getChargerById(chargerId).orElse(null);
            if (selectedCharger != null) {
                model.addAttribute("selectedCharger", selectedCharger);
                availability = chargerService.getSlotAvailability(chargerId, bookingDate);

                // If no available slots left today, check tomorrow
                boolean hasAvailable = availability.getSlots().stream().anyMatch(TimeSlotDto::isAvailable);
                if (!hasAvailable && bookingDate.isEqual(LocalDate.now())) {
                    bookingDate = LocalDate.now().plusDays(1);
                    availability = chargerService.getSlotAvailability(chargerId, bookingDate);
                    model.addAttribute("infoMessage", "All charging slots for today have concluded. Showing tomorrow's available slots.");
                }

                model.addAttribute("availability", availability);

                // Pre-select the first available slot
                for (TimeSlotDto slot : availability.getSlots()) {
                    if (slot.isAvailable()) {
                        defaultStartTime = slot.getStartTime();
                        defaultEndTime = slot.getEndTime();
                        break;
                    }
                }
            }
        }

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStationId(effectiveStationId);
        bookingRequest.setChargerId(chargerId);
        bookingRequest.setBookingDate(bookingDate);
        if (defaultStartTime != null && defaultEndTime != null) {
            bookingRequest.setStartTime(defaultStartTime);
            bookingRequest.setEndTime(defaultEndTime);
        }

        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("selectedDate", bookingDate);
        model.addAttribute("defaultStartTime", defaultStartTime);
        model.addAttribute("defaultEndTime", defaultEndTime);
        return "booking";
    }

    @PostMapping("/create")
    public String createBooking(@Valid @ModelAttribute("bookingRequest") BookingRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Authentication required"));

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder("Please select an available slot: ");
            bindingResult.getAllErrors().forEach(err -> sb.append(err.getDefaultMessage()).append(". "));
            model.addAttribute("errorMessage", sb.toString());
            return newBookingForm(request.getStationId(), request.getChargerId(), request.getBookingDate(), model);
        }

        try {
            Booking booking = bookingService.createBooking(currentUserId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Charging slot reserved! Please complete payment to confirm.");
            return "redirect:/payment/" + booking.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return newBookingForm(request.getStationId(), request.getChargerId(), request.getBookingDate(), model);
        }
    }

    @GetMapping("/{id}")
    public String viewBookingDetails(@PathVariable("id") Long id, Model model) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();

        Booking booking = bookingService.getBookingForUserOrAdmin(id, currentUserId, isAdmin);
        model.addAttribute("booking", booking);
        return "user/booking-details";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id,
                                @RequestParam(value = "reason", required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        Long currentUserId = SecurityUtils.getCurrentUserId().orElse(0L);
        boolean isAdmin = SecurityUtils.isAdmin();

        try {
            bookingService.cancelBooking(id, currentUserId, isAdmin, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cancellation failed: " + e.getMessage());
        }

        if (isAdmin) {
            return "redirect:/admin/bookings";
        }
        return "redirect:/user/my-bookings";
    }
}
