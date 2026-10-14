package com.evcharging.controller.api;

import com.evcharging.dto.AdminDashboardDto;
import com.evcharging.service.AdminReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminReportService reportService;

    public AdminApiController(AdminReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboardSummary() {
        return ResponseEntity.ok(reportService.getDashboardSummary());
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenueReport() {
        return ResponseEntity.ok(reportService.getRevenueReport());
    }

    @GetMapping("/reports/stations")
    public ResponseEntity<List<Map<String, Object>>> getStationReport() {
        return ResponseEntity.ok(reportService.getStationUtilizationReport());
    }
}
