package com.example.demo.controller;

import com.example.demo.DTO.VehicleStatisticsDTO;
import com.example.demo.service.VehicleStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-tracking")
@AllArgsConstructor
public class VehicleStatisticsController {

    private final VehicleStatisticsService vehicleTrackingService;

    @GetMapping("/all")
    public List<VehicleStatisticsDTO> getAllVehicleTracking() {
        return vehicleTrackingService.getAllVehicleTracking();
    }
}
