package com.example.demo.service;

import com.example.demo.model.Camera;
import com.example.demo.model.Violation;
import com.example.demo.model.ViolationType;
import com.example.demo.model.VehicleType;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.ViolationRepository;
import com.example.demo.repository.ViolationTypeRepository;
import com.example.demo.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;
    private final ViolationTypeRepository violationTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Lấy tất cả vi phạm
    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    // Lấy vi phạm theo ID
    public Violation getViolationById(Long id) {
        return violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
    }

    // Lấy lịch sử vi phạm theo biển số
    public List<Violation> getViolationHistory(String licensePlate) {
        return violationRepository.findByLicensePlateOrderByViolationTimeDesc(licensePlate);
    }

    // Tạo vi phạm mới
    public Violation createViolation(Violation violation) {
        if (violation.getViolationType() != null && violation.getViolationType().getId() != null) {
            ViolationType violationType = violationTypeRepository.findById(violation.getViolationType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại"));
            violation.setViolationType(violationType);
        }
        if (violation.getVehicleType() != null && violation.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(violation.getVehicleType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại"));
            violation.setVehicleType(vehicleType);
        }
        if (violation.getCamera() != null && violation.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(violation.getCamera().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại"));
            violation.setCamera(camera);
        }
        return violationRepository.save(violation);
    }

    // Cập nhật vi phạm
    public Violation updateViolation(Long id, Violation updated) {
        Violation existingViolation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));

        // Cập nhật các trường quan hệ
        if (updated.getViolationType() != null && updated.getViolationType().getId() != null) {
            ViolationType violationType = violationTypeRepository.findById(updated.getViolationType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + updated.getViolationType().getId()));
            existingViolation.setViolationType(violationType);
        }
        if (updated.getVehicleType() != null && updated.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(updated.getVehicleType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + updated.getVehicleType().getId()));
            existingViolation.setVehicleType(vehicleType);
        }
        if (updated.getCamera() != null && updated.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(updated.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + updated.getCamera().getId()));
            existingViolation.setCamera(camera);
        }

        // Cập nhật các trường khác
        if (updated.getLicensePlate() != null) {
            existingViolation.setLicensePlate(updated.getLicensePlate());
        }
        if (updated.getVehicleColor() != null) {
            existingViolation.setVehicleColor(updated.getVehicleColor());
        }
        if (updated.getVehicleBrand() != null) {
            existingViolation.setVehicleBrand(updated.getVehicleBrand());
        }
        if (updated.getImageUrl() != null) {
            existingViolation.setImageUrl(updated.getImageUrl());
        }
        if (updated.getVideoUrl() != null) {
            existingViolation.setVideoUrl(updated.getVideoUrl());
        }
        if (updated.getViolationTime() != null) {
            existingViolation.setViolationTime(updated.getViolationTime());
        }

        return violationRepository.save(existingViolation);
    }

    // Xóa vi phạm
    public void deleteViolation(Long id) {

        violationRepository.deleteById(id);
    }

    // Lấy tất cả loại vi phạm
    public List<ViolationType> getAllViolationTypes() {
        return violationTypeRepository.findAll();
    }

    // Lấy tất cả loại xe
    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll();
    }

    // Tạo loại vi phạm mới
    public ViolationType createViolationType(ViolationType violationType) {
        if (violationType.getTypeName() == null || violationType.getTypeName().isEmpty()) {
            throw new IllegalArgumentException("Tên loại vi phạm không được để trống");
        }
        return violationTypeRepository.save(violationType);
    }

    // Cập nhật loại vi phạm
    public ViolationType updateViolationType(Long id, ViolationType updatedType) {
        ViolationType existingType = violationTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + id));
        if (updatedType.getTypeName() != null && !updatedType.getTypeName().isEmpty()) {
            existingType.setTypeName(updatedType.getTypeName());
        }
        if (updatedType.getDescription() != null) {
            existingType.setDescription(updatedType.getDescription());
        }
        return violationTypeRepository.save(existingType);
    }
}