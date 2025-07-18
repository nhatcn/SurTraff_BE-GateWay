package com.example.demo.controller;

import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.model.VehicleType;
import com.example.demo.model.ViolationType;
import com.example.demo.service.ViolationService;
import jakarta.persistence.EntityNotFoundException;
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
        try {
            List<ViolationsDTO> violations = violationService.getAllViolations();
            return ResponseEntity.ok(violations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolationsDTO> getViolationById(@PathVariable Long id) {
        try {
            ViolationsDTO violation = violationService.getViolationById(id);
            return violation != null ? ResponseEntity.ok(violation) : ResponseEntity.notFound().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{licensePlate}")
    public ResponseEntity<List<ViolationsDTO>> getViolationHistory(@PathVariable String licensePlate) {
        try {
            List<ViolationsDTO> history = violationService.getViolationHistory(licensePlate);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationsDTO> createViolation(@Valid @RequestBody ViolationsDTO dto) {
        try {
            ViolationsDTO createdViolation = violationService.createViolation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdViolation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByUserId(@PathVariable Long userId) {
        try {
            List<ViolationsDTO> violations = violationService.getAllViolationsByUserId(userId);
            return ResponseEntity.ok(violations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationsDTO> updateViolation(@PathVariable Long id, @Valid @RequestBody ViolationsDTO dto) {
        try {
            ViolationsDTO updatedViolation = violationService.updateViolation(id, dto);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteViolation(@PathVariable Long id) {
        try {
            violationService.deleteViolation(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/violation-types")
    public ResponseEntity<List<ViolationType>> getAllViolationTypes() {
        try {
            List<ViolationType> violationTypes = violationService.getAllViolationTypes();
            return ResponseEntity.ok(violationTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/vehicle-types")
    public ResponseEntity<List<VehicleType>> getAllVehicleTypes() {
        try {
            List<VehicleType> vehicleTypes = violationService.getAllVehicleTypes();
            return ResponseEntity.ok(vehicleTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/violation-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationType> createViolationType(@Valid @RequestBody ViolationType violationType) {
        try {
            ViolationType createdType = violationService.createViolationType(violationType);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/violation-types/{id}")
    public ResponseEntity<ViolationType> updateViolationType(@PathVariable Long id, @Valid @RequestBody ViolationType violationType) {
        try {
            ViolationType updatedType = violationService.updateViolationType(id, violationType);
            return ResponseEntity.ok(updatedType);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/{id}/details")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationDetailDTO> addViolationDetail(
            @PathVariable Long id,
            @Valid @RequestBody ViolationDetailDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) {
        try {
            ViolationDetailDTO createdDetail = violationService.addViolationDetail(id, dto, imageFile, videoFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDetail);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<ViolationDetailDTO> updateViolationDetail(
            @PathVariable Long detailId,
            @Valid @RequestBody ViolationDetailDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) {
        try {
            ViolationDetailDTO updatedDetail = violationService.updateViolationDetail(detailId, dto, imageFile, videoFile);
            return ResponseEntity.ok(updatedDetail);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/details/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteViolationDetail(@PathVariable Long detailId) {
        try {
            violationService.deleteViolationDetail(detailId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/license-plate/{licensePlate}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByLicensePlate(@PathVariable String licensePlate) {
        try {
            List<ViolationsDTO> violations = violationService.getViolationsByLicensePlate(licensePlate);
            return ResponseEntity.ok(violations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByStatus(@PathVariable String status) {
        try {
            List<ViolationsDTO> allViolations = violationService.getAllViolations();
            List<ViolationsDTO> filteredViolations = allViolations.stream()
                    .filter(v -> v.getStatus() != null && v.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filteredViolations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}