package com.example.demo.controller;

import com.example.demo.model.Violation;
import com.example.demo.model.ViolationType;
import com.example.demo.model.VehicleType;
import com.example.demo.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://green-sigma-one.vercel.app"})
public class ViolationController {

    private final ViolationService violationService;

    @GetMapping
    public List<Violation> getAllViolations() {
        return violationService.getAllViolations();
    }

    @GetMapping("/{id}")
    public Violation getViolationById(@PathVariable Long id) {
        return violationService.getViolationById(id);
    }

    @GetMapping("/history/{plate}")
    public List<Violation> getHistory(@PathVariable String plate) {
        return violationService.getViolationHistory(plate);
    }

    @PostMapping
    public Violation createViolation(@RequestBody Violation violation) {
        return violationService.createViolation(violation);
    }

    @PutMapping("/{id}")
    public Violation updateViolation(@PathVariable Long id, @RequestBody Violation violation) {
        return violationService.updateViolation(id, violation);
    }

    @DeleteMapping("/{id}")
    public void deleteViolation(@PathVariable Long id) {
        violationService.deleteViolation(id);
    }

    @GetMapping("/violationtypes")
    public List<ViolationType> getAllViolationTypes() {
        return violationService.getAllViolationTypes();
    }

    @GetMapping("/vehicletypes")
    public List<VehicleType> getAllVehicleTypes() {
        return violationService.getAllVehicleTypes();
    }

    @PostMapping("/violation-types")
    public ViolationType createViolationType(@RequestBody ViolationType violationType) {
        return violationService.createViolationType(violationType);
    }

    @PutMapping("/violation-types/{id}")
    public ViolationType updateViolationType(@PathVariable Long id, @RequestBody ViolationType violationType) {
        return violationService.updateViolationType(id, violationType);
    }
}