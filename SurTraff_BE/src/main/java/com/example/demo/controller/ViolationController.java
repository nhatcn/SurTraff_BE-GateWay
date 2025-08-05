package com.example.demo.controller;

import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.model.VehicleType;
import com.example.demo.model.ViolationType;
import com.example.demo.service.ViolationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// Lớp phản hồi lỗi
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private static final Logger logger = LoggerFactory.getLogger(ViolationController.class);

    @Autowired
    private ViolationService violationService;

    @GetMapping
    public ResponseEntity<List<ViolationsDTO>> getAllViolations() {
        try {
            List<ViolationsDTO> violations = violationService.getAllViolations();
            return ResponseEntity.ok(violations);
        } catch (Exception e) {
            logger.error("Error fetching all violations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolationsDTO> getViolationById(@PathVariable Long id) {
        try {
            ViolationsDTO violation = violationService.getViolationById(id);
            return violation != null ? ResponseEntity.ok(violation) : ResponseEntity.notFound().build();
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{licensePlate}")
    public ResponseEntity<List<ViolationsDTO>> getViolationHistory(@PathVariable String licensePlate) {
        try {
            List<ViolationsDTO> history = violationService.getViolationHistory(licensePlate);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid license plate: {}", licensePlate, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error fetching violation history for license plate: {}", licensePlate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationsDTO> createViolation(
            @Valid @RequestPart("Violation") ViolationsDTO dto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile) {
        try {
            ViolationsDTO createdViolation = violationService.createViolationNhat(dto, imageFile, videoFile);
            logger.info("Created violation with ID: {}", createdViolation.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdViolation);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for creating violation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            logger.error("IO error while creating violation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Error creating violation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByUserId(@PathVariable Long userId) {
        try {
            List<ViolationsDTO> violations = violationService.getAllViolationsByUserId(userId);
            return ResponseEntity.ok(violations);
        } catch (EntityNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching violations for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationsDTO> updateViolation(@PathVariable Long id, @Valid @RequestBody ViolationsDTO dto) {
        try {
            ViolationsDTO updatedViolation = violationService.updateViolation(id, dto);
            logger.info("Updated violation with ID: {}", id);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for updating violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error updating violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteViolation(@PathVariable Long id) {
        try {
            violationService.deleteViolation(id);
            logger.info("Deleted violation with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error deleting violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/violation-types")
    public ResponseEntity<List<ViolationType>> getAllViolationTypes() {
        try {
            List<ViolationType> violationTypes = violationService.getAllViolationTypes();
            return ResponseEntity.ok(violationTypes);
        } catch (Exception e) {
            logger.error("Error fetching violation types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/vehicle-types")
    public ResponseEntity<List<VehicleType>> getAllVehicleTypes() {
        try {
            List<VehicleType> vehicleTypes = violationService.getAllVehicleTypes();
            return ResponseEntity.ok(vehicleTypes);
        } catch (Exception e) {
            logger.error("Error fetching vehicle types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/violation-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ViolationType> createViolationType(@Valid @RequestBody ViolationType violationType) {
        try {
            ViolationType createdType = violationService.createViolationType(violationType);
            logger.info("Created violation type with ID: {}", createdType.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for creating violation type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error creating violation type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/violation-types/{id}")
    public ResponseEntity<ViolationType> updateViolationType(@PathVariable Long id, @Valid @RequestBody ViolationType violationType) {
        try {
            ViolationType updatedType = violationService.updateViolationType(id, violationType);
            logger.info("Updated violation type with ID: {}", id);
            return ResponseEntity.ok(updatedType);
        } catch (EntityNotFoundException e) {
            logger.error("Violation type not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for updating violation type ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error updating violation type ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
            logger.info("Added violation detail for violation ID: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDetail);
        } catch (IOException e) {
            logger.error("IO error adding violation detail for violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for adding violation detail for violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error adding violation detail for violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
            logger.info("Updated violation detail with ID: {}", detailId);
            return ResponseEntity.ok(updatedDetail);
        } catch (IOException e) {
            logger.error("IO error updating violation detail ID: {}", detailId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (EntityNotFoundException e) {
            logger.error("Violation detail not found with ID: {}", detailId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for updating violation detail ID: {}: {}", detailId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error updating violation detail ID: {}", detailId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/details/{detailId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteViolationDetail(@PathVariable Long detailId) {
        try {
            violationService.deleteViolationDetail(detailId);
            logger.info("Deleted violation detail with ID: {}", detailId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            logger.error("Violation detail not found with ID: {}", detailId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error deleting violation detail ID: {}", detailId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/license-plate/{licensePlate}")
    public ResponseEntity<List<ViolationsDTO>> getViolationsByLicensePlate(@PathVariable String licensePlate) {
        try {
            List<ViolationsDTO> violations = violationService.getViolationsByLicensePlate(licensePlate);
            return ResponseEntity.ok(violations);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid license plate: {}", licensePlate, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (EntityNotFoundException e) {
            logger.error("No violations found for license plate: {}", licensePlate, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching violations for license plate: {}", licensePlate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<ViolationsDTO> requestViolation(@PathVariable Long id) {
        logger.info("Received request for violation ID: {}", id);
        try {
            ViolationsDTO updatedViolation = violationService.requestViolation(id);
            logger.info("Violation ID: {} status updated to REQUESTED", id);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ViolationsDTO());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for requesting violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ViolationsDTO());
        } catch (Exception e) {
            logger.error("Error requesting violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ViolationsDTO());
        }
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<ViolationsDTO> processViolation(@PathVariable Long id) {
        logger.info("Received process request for violation ID: {}", id);
        try {
            ViolationsDTO updatedViolation = violationService.processViolation(id);
            logger.info("Violation ID: {} status updated to PROCESSED", id);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ViolationsDTO());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for processing violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ViolationsDTO());
        } catch (Exception e) {
            logger.error("Error processing violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ViolationsDTO());
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ViolationsDTO> approveViolation(@PathVariable Long id) {
        logger.info("Received approve request for violation ID: {}", id);
        try {
            ViolationsDTO updatedViolation = violationService.approveViolation(id);
            logger.info("Violation ID: {} status updated to APPROVED", id);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ViolationsDTO());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for approving violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ViolationsDTO());
        } catch (Exception e) {
            logger.error("Error approving violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ViolationsDTO());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ViolationsDTO> rejectViolation(@PathVariable Long id) {
        logger.info("Received reject request for violation ID: {}", id);
        try {
            ViolationsDTO updatedViolation = violationService.rejectViolation(id);
            logger.info("Violation ID: {} status updated to REJECTED", id);
            return ResponseEntity.ok(updatedViolation);
        } catch (EntityNotFoundException e) {
            logger.error("Violation not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ViolationsDTO());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for rejecting violation ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ViolationsDTO());
        } catch (Exception e) {
            logger.error("Error rejecting violation ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ViolationsDTO());
        }
    }
}