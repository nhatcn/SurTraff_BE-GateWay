package com.example.demo.controller;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public VehicleDTO getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PostMapping
    public VehicleDTO createVehicle(@RequestBody VehicleDTO dto) {
        return vehicleService.createVehicle(dto);
    }

    @PutMapping("/{id}")
    public VehicleDTO updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO dto) {
        return vehicleService.updateVehicle(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByUserId(@PathVariable Long userId) {
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }
}