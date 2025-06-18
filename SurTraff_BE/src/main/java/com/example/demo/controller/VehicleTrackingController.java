package com.example.demo.controller;

import com.example.demo.DTO.VehicleTrackingDTO;
import com.example.demo.service.VehicleTrackingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-tracking")
@AllArgsConstructor
public class VehicleTrackingController {

    private final VehicleTrackingService vehicleTrackingService;

    @GetMapping("/all")
    public List<VehicleTrackingDTO> getAllVehicleTracking() {
        return vehicleTrackingService.getAllVehicleTracking();
    }
}
