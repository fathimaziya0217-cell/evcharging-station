package com.evcharging.controller;

import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Station;
import com.evcharging.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final StationService stationService;

    public HomeController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Fetch popular or featured active stations from MySQL
        List<Station> stations = stationService.getAllStations(true);
        List<Station> popularStations = stations.stream().limit(6).toList();

        model.addAttribute("popularStations", popularStations);
        model.addAttribute("cities", stationService.getAllCities());
        model.addAttribute("searchCriteria", new StationSearchCriteria());
        model.addAttribute("totalStationsCount", stationService.countTotalStations());
        return "index";
    }

    @GetMapping("/how-it-works")
    public String howItWorks() {
        return "how-it-works";
    }
}
