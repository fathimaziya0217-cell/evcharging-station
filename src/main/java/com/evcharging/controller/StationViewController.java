package com.evcharging.controller;

import com.evcharging.dto.StationSearchCriteria;
import com.evcharging.entity.Station;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stations")
public class StationViewController {

    private final StationService stationService;

    public StationViewController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public String findStations(@ModelAttribute("criteria") StationSearchCriteria criteria, Model model) {
        List<Station> stations = stationService.searchStations(criteria);

        model.addAttribute("stations", stations);
        model.addAttribute("criteria", criteria);
        model.addAttribute("cities", stationService.getAllCities());
        model.addAttribute("totalFound", stations.size());
        return "stations";
    }

    @GetMapping("/{id}")
    public String stationDetails(@PathVariable("id") Long id, Model model) {
        Station station = stationService.getStationDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging station not found with ID: " + id));

        model.addAttribute("station", station);
        model.addAttribute("chargers", station.getChargers());
        return "station-details";
    }
}
