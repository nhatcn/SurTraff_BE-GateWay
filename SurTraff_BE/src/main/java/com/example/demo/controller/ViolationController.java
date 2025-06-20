package com.example.demo.controller;

import com.example.demo.model.VehicleType;
import com.example.demo.model.Violation;
import com.example.demo.model.ViolationDetail;
import com.example.demo.model.ViolationType;
import com.example.demo.service.ViolationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@AllArgsConstructor
public class ViolationController {

    private final ViolationService violationService;

    @GetMapping
    public List<Violation> getAllViolations() {
        return violationService.getAllViolations();
    }

    @GetMapping("/{id}")
    public Violation getViolationById(@PathVariable Integer id) {
        return violationService.getViolationById(id);
    }

    @GetMapping("/history/{licensePlate}")
    public List<Violation> getHistory(@PathVariable String licensePlate) {
        return violationService.getViolationHistory(licensePlate);
    }

    @PostMapping
    public Violation createViolation(@RequestBody Violation violation) {
        return violationService.createViolation(violation);
    }

    @PutMapping("/{id}")
    public Violation updateViolation(@PathVariable Integer id, @RequestBody Violation violation) {
        return violationService.updateViolation(id, violation);
    }

    @DeleteMapping("/{id}")
    public void deleteViolation(@PathVariable Integer id) {
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
    public ViolationType updateViolationType(@PathVariable Integer id, @RequestBody ViolationType violationType) {
        return violationService.updateViolationType(id, violationType);
    }

    @PostMapping("/{id}/details")
    public ViolationDetail addViolationDetail(@PathVariable Integer id, @RequestBody ViolationDetail detail) {
        return violationService.addViolationDetail(id, detail);
    }

    @PutMapping("/details/{detailId}")
    public ViolationDetail updateViolationDetail(@PathVariable Integer detailId, @RequestBody ViolationDetail detail) {
        return violationService.updateViolationDetail(detailId, detail);
    }

    @DeleteMapping("/details/{detailId}")
    public void deleteViolationDetail(@PathVariable Integer detailId) {
        violationService.deleteViolationDetail(detailId);
    }
}