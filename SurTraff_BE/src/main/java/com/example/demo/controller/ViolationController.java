package com.example.demo.controller;

import com.example.demo.DTO.ViolationDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.model.VehicleType;
import com.example.demo.model.ViolationType;
import com.example.demo.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    @GetMapping
    public List<ViolationsDTO> getAllViolations() {
        return violationService.getAllViolations();
    }

    @GetMapping("/{id}")
    public ViolationsDTO getViolationById(@PathVariable Long id) {
        return violationService.getViolationById(id);
    }

    @GetMapping("/history/{licensePlate}")
    public List<ViolationsDTO> getHistory(@PathVariable String licensePlate) {
        return violationService.getViolationHistory(licensePlate);
    }

    @PostMapping
    public ViolationsDTO addViolation(@RequestBody ViolationsDTO dto) {
        return violationService.createViolation(dto);
    }

    @GetMapping("/user/{userId}")
    public List<ViolationsDTO> getAllViolationsByUserId(@PathVariable Long userId) {
        return violationService.getAllViolationsByUserId(userId);
    }

    @PutMapping("/{id}")
    public ViolationsDTO updateViolation(@PathVariable Long id, @RequestBody ViolationsDTO dto) {
        return violationService.updateViolation(id, dto);
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

    @PostMapping("/{id}/details")
    public ViolationDetailDTO addViolationDetail(@PathVariable Long id, @RequestBody ViolationDetailDTO dto) {
        return violationService.addViolationDetail(id, dto);
    }

    @PutMapping("/details/{detailId}")
    public ViolationDetailDTO updateViolationDetail(@PathVariable Long detailId, @RequestBody ViolationDetailDTO dto) {
        return violationService.updateViolationDetail(detailId, dto);
    }

    @DeleteMapping("/details/{detailId}")
    public void deleteViolationDetail(@PathVariable Long detailId) {
        violationService.deleteViolationDetail(detailId);
    }

    @GetMapping("/licenseplate")
    public List<ViolationDTO> getViolationsByLicensePlate(@RequestParam(required = false) String licensePlate) {
        return violationService.getViolationsByLicensePlate(licensePlate);
    }
}