package com.example.demo.controller;

import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.model.VehicleType;
import com.example.demo.model.ViolationType;
import com.example.demo.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    @GetMapping
    public ResponseEntity<List<ViolationsDTO>> getAllViolations() {
        List<ViolationsDTO> violations = violationService.getAllViolations();
        return ResponseEntity.ok(violations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolationsDTO> getViolationById(@PathVariable Long id) {
        ViolationsDTO violation = violationService.getViolationById(id);
        return ResponseEntity.ok(violation);
    }

    @GetMapping("/history/{licensePlate}")
    public ResponseEntity<List<ViolationsDTO>> getHistory(@PathVariable String licensePlate) {
        List<ViolationsDTO> history = violationService.getViolationHistory(licensePlate);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationsDTO> addViolation(@Valid @RequestBody ViolationsDTO dto) {
        ViolationsDTO createdViolation = violationService.createViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdViolation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViolationsDTO>> getAllViolationsByUserId(@PathVariable Long userId) {
        List<ViolationsDTO> violations = violationService.getAllViolationsByUserId(userId);
        return ResponseEntity.ok(violations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationsDTO> updateViolation(@PathVariable Long id, @Valid @RequestBody ViolationsDTO dto) {
        ViolationsDTO updatedViolation = violationService.updateViolation(id, dto);
        return ResponseEntity.ok(updatedViolation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteViolation(@PathVariable Long id) {
        violationService.deleteViolation(id);
    }

    @GetMapping("/violationtypes")
    public ResponseEntity<List<ViolationType>> getAllViolationTypes() {
        List<ViolationType> violationTypes = violationService.getAllViolationTypes();
        return ResponseEntity.ok(violationTypes);
    }

    @GetMapping("/vehicletypes")
    public ResponseEntity<List<VehicleType>> getAllVehicleTypes() {
        List<VehicleType> vehicleTypes = violationService.getAllVehicleTypes();
        return ResponseEntity.ok(vehicleTypes);
    }

    @PostMapping("/violation-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationType> createViolationType(@Valid @RequestBody ViolationType violationType) {
        ViolationType createdType = violationService.createViolationType(violationType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
    }

    @PutMapping("/violation-types/{id}")
    public ResponseEntity<ViolationType> updateViolationType(@PathVariable Long id, @Valid @RequestBody ViolationType violationType) {
        ViolationType updatedType = violationService.updateViolationType(id, violationType);
        return ResponseEntity.ok(updatedType);
    }

    @PostMapping("/{id}/details")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationDetailDTO> addViolationDetail(
            @PathVariable Long id,
            @Valid @RequestPart("dto") ViolationDetailDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) throws IOException {
        ViolationDetailDTO createdDetail = violationService.addViolationDetail(id, dto, imageFile, videoFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDetail);
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<ViolationDetailDTO> updateViolationDetail(
            @PathVariable Long detailId,
            @Valid @RequestPart("dto") ViolationDetailDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) throws IOException {
        ViolationDetailDTO updatedDetail = violationService.updateViolationDetail(detailId, dto, imageFile, videoFile);
        return ResponseEntity.ok(updatedDetail);
    }

    @DeleteMapping("/details/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteViolationDetail(@PathVariable Long detailId) {
        violationService.deleteViolationDetail(detailId);
    }

    @GetMapping("/licenseplate")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByLicensePlate(@RequestParam String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống");
        }
        List<ViolationsDTO> violations = violationService.getViolationsByLicensePlate(licensePlate);
        return ResponseEntity.ok(violations);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByStatus(@PathVariable String status) {
        List<ViolationsDTO> allViolations = violationService.getAllViolations();
        List<ViolationsDTO> filteredViolations = allViolations.stream()
                .filter(v -> v.getStatus() != null && v.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredViolations);
    }
}