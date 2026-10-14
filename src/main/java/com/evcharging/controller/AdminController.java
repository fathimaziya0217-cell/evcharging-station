package com.evcharging.controller;

import com.evcharging.dto.*;
import com.evcharging.entity.*;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.SettingsRepository;
import com.evcharging.security.SecurityUtils;
import com.evcharging.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final StationService stationService;
    private final ChargerService chargerService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ChargingSessionService sessionService;
    private final AdminReportService reportService;
    private final AuditLogService auditLogService;
    private final SettingsRepository settingsRepository;

    public AdminController(UserService userService,
                           StationService stationService,
                           ChargerService chargerService,
                           BookingService bookingService,
                           PaymentService paymentService,
                           ChargingSessionService sessionService,
                           AdminReportService reportService,
                           AuditLogService auditLogService,
                           SettingsRepository settingsRepository) {
        this.userService = userService;
        this.stationService = stationService;
        this.chargerService = chargerService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.sessionService = sessionService;
        this.reportService = reportService;
        this.auditLogService = auditLogService;
        this.settingsRepository = settingsRepository;
    }

    // ------------------------------------------------------------------------
    // 1. Admin Dashboard
    // ------------------------------------------------------------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AdminDashboardDto summary = reportService.getDashboardSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("recentBookings", bookingService.getAllBookings().stream().limit(6).toList());
        model.addAttribute("recentAuditLogs", auditLogService.getRecentLogs(5));
        return "admin/dashboard";
    }

    // ------------------------------------------------------------------------
    // 2. User Management
    // ------------------------------------------------------------------------
    @GetMapping("/users")
    public String manageUsers(@RequestParam(value = "query", required = false) String query, Model model) {
        List<User> users = userService.searchUsers(query);
        model.addAttribute("users", users);
        model.addAttribute("query", query);
        return "admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id,
                                   @RequestParam("active") boolean active,
                                   RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        userService.toggleUserActive(id, active);
        auditLogService.log(adminId, active ? "ACTIVATE_USER" : "DEACTIVATE_USER", "USER", String.valueOf(id),
                "User account " + (active ? "activated" : "deactivated"), null);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated successfully.");
        return "redirect:/admin/users";
    }

    // ------------------------------------------------------------------------
    // 3. Station Management
    // ------------------------------------------------------------------------
    @GetMapping("/stations")
    public String manageStations(Model model) {
        List<Station> stations = stationService.getAllStations(false);
        model.addAttribute("stations", stations);
        return "admin/stations";
    }

    @GetMapping("/stations/new")
    public String newStationForm(Model model) {
        model.addAttribute("stationForm", new StationFormDto());
        return "admin/station-form";
    }

    @GetMapping("/stations/edit/{id}")
    public String editStationForm(@PathVariable("id") Long id, Model model) {
        Station s = stationService.getStationDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + id));

        StationFormDto dto = new StationFormDto();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setAddress(s.getAddress());
        dto.setArea(s.getArea());
        dto.setCity(s.getCity());
        dto.setState(s.getState());
        dto.setPostalCode(s.getPostalCode());
        dto.setLatitude(s.getLatitude());
        dto.setLongitude(s.getLongitude());
        dto.setContactNumber(s.getContactNumber());
        dto.setOpeningTime(s.getOpeningTime());
        dto.setClosingTime(s.getClosingTime());
        dto.setOperatingDays(s.getOperatingDays());
        dto.setParkingInformation(s.getParkingInformation());
        dto.setStatus(s.getStatus());
        dto.setDemo(s.isDemo());
        dto.setAmenitiesCsv(String.join(", ", s.getAmenities()));

        model.addAttribute("stationForm", dto);
        return "admin/station-form";
    }

    @PostMapping("/stations/save")
    public String saveStation(@Valid @ModelAttribute("stationForm") StationFormDto dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/station-form";
        }
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        Station saved = stationService.saveOrUpdateStation(dto);
        auditLogService.log(adminId, dto.getId() == null ? "CREATE_STATION" : "UPDATE_STATION",
                "STATION", String.valueOf(saved.getId()), "Station saved: " + saved.getName(), null);
        redirectAttributes.addFlashAttribute("successMessage", "Station '" + saved.getName() + "' saved successfully!");
        return "redirect:/admin/stations";
    }

    @PostMapping("/stations/toggle/{id}")
    public String toggleStationStatus(@PathVariable("id") Long id,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        stationService.toggleStationStatus(id, status);
        auditLogService.log(adminId, "TOGGLE_STATION_STATUS", "STATION", String.valueOf(id),
                "Station status set to: " + status, null);
        redirectAttributes.addFlashAttribute("successMessage", "Station status updated to " + status);
        return "redirect:/admin/stations";
    }

    // ------------------------------------------------------------------------
    // 4. Charger Management
    // ------------------------------------------------------------------------
    @GetMapping("/chargers")
    public String manageChargers(@RequestParam(value = "stationId", required = false) Long stationId, Model model) {
        List<Charger> chargers = (stationId != null) ?
                chargerService.getChargersByStation(stationId) :
                chargerService.getAllChargers();

        model.addAttribute("chargers", chargers);
        model.addAttribute("stations", stationService.getAllStations(false));
        model.addAttribute("selectedStationId", stationId);
        model.addAttribute("newCharger", new ChargerFormDto());
        return "admin/chargers";
    }

    @PostMapping("/chargers/save")
    public String saveCharger(@Valid @ModelAttribute("newCharger") ChargerFormDto dto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid charger form data");
            return "redirect:/admin/chargers";
        }
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        Charger saved = chargerService.saveOrUpdateCharger(dto);
        auditLogService.log(adminId, dto.getId() == null ? "CREATE_CHARGER" : "UPDATE_CHARGER",
                "CHARGER", String.valueOf(saved.getId()), "Charger saved: " + saved.getChargerIdentifier(), null);
        redirectAttributes.addFlashAttribute("successMessage", "Charger saved successfully!");
        return "redirect:/admin/chargers?stationId=" + saved.getStationId();
    }

    @PostMapping("/chargers/status/{id}")
    public String updateChargerStatus(@PathVariable("id") Long id,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        chargerService.updateOperationalStatus(id, status);
        auditLogService.log(adminId, "UPDATE_CHARGER_STATUS", "CHARGER", String.valueOf(id),
                "Charger operational status changed to: " + status, null);
        redirectAttributes.addFlashAttribute("successMessage", "Charger operational status updated.");
        return "redirect:/admin/chargers";
    }

    // ------------------------------------------------------------------------
    // 5. Booking Management
    // ------------------------------------------------------------------------
    @GetMapping("/bookings")
    public String manageBookings(@RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {
        List<Booking> bookings = bookingService.searchBookings(query, status, date);
        model.addAttribute("bookings", bookings);
        model.addAttribute("query", query);
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        return "admin/bookings";
    }

    // ------------------------------------------------------------------------
    // 6. Charging Sessions Management (Simulation for College Project)
    // ------------------------------------------------------------------------
    @GetMapping("/sessions")
    public String manageSessions(Model model) {
        List<ChargingSession> sessions = sessionService.getAllSessions();
        model.addAttribute("sessions", sessions);
        model.addAttribute("simulateDto", new SessionSimulateDto());
        return "admin/sessions";
    }

    @PostMapping("/sessions/start/{id}")
    public String startSession(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        try {
            sessionService.startSession(id, adminId);
            auditLogService.log(adminId, "START_SESSION_SIMULATION", "SESSION", String.valueOf(id),
                    "Simulated start of charging session", null);
            redirectAttributes.addFlashAttribute("successMessage", "Charging session started! Charger status set to OCCUPIED.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error starting session: " + e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    @PostMapping("/sessions/complete")
    public String completeSession(@Valid @ModelAttribute("simulateDto") SessionSimulateDto dto,
                                  RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        try {
            sessionService.completeSession(dto, adminId);
            auditLogService.log(adminId, "COMPLETE_SESSION_SIMULATION", "SESSION", String.valueOf(dto.getSessionId()),
                    "Simulated session completion with " + dto.getEnergyConsumedKwh() + " kWh energy consumed", null);
            redirectAttributes.addFlashAttribute("successMessage", "Charging session completed! Energy consumed recorded and charger released.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing session: " + e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    // ------------------------------------------------------------------------
    // 7. Payment Transactions Management
    // ------------------------------------------------------------------------
    @GetMapping("/payments")
    public String managePayments(@RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "status", required = false) String status,
                                 Model model) {
        List<Payment> payments = paymentService.searchPayments(query, status);
        model.addAttribute("payments", payments);
        model.addAttribute("totalRevenue", paymentService.getTotalSuccessfulRevenue());
        model.addAttribute("query", query);
        model.addAttribute("status", status);
        return "admin/payments";
    }

    // ------------------------------------------------------------------------
    // 8. Reports & CSV Export
    // ------------------------------------------------------------------------
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("utilizationReport", reportService.getStationUtilizationReport());
        model.addAttribute("revenueReport", reportService.getRevenueReport());
        model.addAttribute("cancellationReport", reportService.getCancellationReport());
        model.addAttribute("userReport", reportService.getUserRegistrationReport());
        return "admin/reports";
    }

    @GetMapping("/reports/export/{type}")
    public void exportReportCsv(@PathVariable("type") String type, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        String filename = "ev_charge_report_" + type + "_" + LocalDate.now() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        String csvData = switch (type.toLowerCase()) {
            case "stations" -> reportService.exportStationUtilizationCsv();
            case "revenue" -> reportService.exportRevenueCsv();
            case "cancellations" -> reportService.exportCancellationsCsv();
            case "users" -> reportService.exportUsersCsv();
            default -> "Error: Unknown report type\r\n";
        };

        response.getWriter().write(csvData);
        response.getWriter().flush();
    }

    // ------------------------------------------------------------------------
    // 9. Application Settings
    // ------------------------------------------------------------------------
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settingsList", settingsRepository.findAll());
        return "admin/settings";
    }

    @PostMapping("/settings/save")
    public String saveSetting(@RequestParam("settingKey") String key,
                              @RequestParam("settingValue") String value,
                              RedirectAttributes redirectAttributes) {
        Long adminId = SecurityUtils.getCurrentUserId().orElse(1L);
        settingsRepository.updateValue(key, value);
        auditLogService.log(adminId, "UPDATE_SETTING", "SETTING", key, "Updated value to: " + value, null);
        redirectAttributes.addFlashAttribute("successMessage", "Setting '" + key + "' updated successfully.");
        return "redirect:/admin/settings";
    }

    // ------------------------------------------------------------------------
    // 10. Audit Logs
    // ------------------------------------------------------------------------
    @GetMapping("/audit-logs")
    public String auditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getRecentLogs(50));
        return "admin/audit-logs";
    }
}
