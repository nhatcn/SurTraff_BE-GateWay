package com.example.demo.service;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;
    private final ViolationTypeRepository violationTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final ViolationDetailRepository violationDetailRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository,
                            ViolationTypeRepository violationTypeRepository, VehicleTypeRepository vehicleTypeRepository,
                            VehicleRepository vehicleRepository, ViolationDetailRepository violationDetailRepository) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
        this.violationTypeRepository = violationTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.violationDetailRepository = violationDetailRepository;
    }

    // Lấy tất cả vi phạm
    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    // Lấy vi phạm theo ID
    public Violation getViolationById(Integer id) {
        return violationRepository.findById(id.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
    }

    // Lấy lịch sử vi phạm theo biển số
    public List<Violation> getViolationHistory(String licensePlate) {
        return violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate);
    }


    // Tạo vi phạm mới
    public Violation createViolation(Violation violation) {
        // Kiểm tra và thiết lập Camera
        if (violation.getCamera() != null && violation.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(violation.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + violation.getCamera().getId()));
            violation.setCamera(camera);
        }
        // Kiểm tra và thiết lập VehicleType
        if (violation.getVehicleType() != null && violation.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(violation.getVehicleType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + violation.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }
        // Kiểm tra và thiết lập Vehicle
        if (violation.getVehicle() != null && violation.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(violation.getVehicle().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + violation.getVehicle().getId()));
            violation.setVehicle(vehicle);
        }
        // Thiết lập ViolationDetail
        if (violation.getViolationDetails() != null) {
            for (ViolationDetail detail : violation.getViolationDetails()) {
                detail.setViolation(violation);
                if (detail.getViolationType() != null && detail.getViolationType().getId() != null) {
                    ViolationType violationType = violationTypeRepository.findById(detail.getViolationType().getId().longValue())
                            .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + detail.getViolationType().getId()));
                    detail.setViolationType(violationType);
                }
            }
        }
        return violationRepository.save(violation);
    }

    // Cập nhật vi phạm
    public Violation updateViolation(Integer id, Violation updated) {
        Violation existingViolation = violationRepository.findById(id.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));

        // Cập nhật Camera
        if (updated.getCamera() != null && updated.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(updated.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + updated.getCamera().getId()));
            existingViolation.setCamera(camera);
        }
        // Cập nhật VehicleType
        if (updated.getVehicleType() != null && updated.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(updated.getVehicleType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + updated.getVehicleType().getId()));
            existingViolation.setVehicleType(vehicleType);
        }
        // Cập nhật Vehicle
        if (updated.getVehicle() != null && updated.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(updated.getVehicle().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + updated.getVehicle().getId()));
            existingViolation.setVehicle(vehicle);
        }
        // Cập nhật ViolationDetails (không cập nhật trực tiếp qua API này, dùng endpoint riêng)
        return violationRepository.save(existingViolation);
    }

    // Xóa vi phạm
    public void deleteViolation(Integer id) {
        if (!violationRepository.existsById(id.longValue())) {
            throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
        }
        violationRepository.deleteById(id.longValue());
    }

    // Thêm chi tiết vi phạm
    public ViolationDetail addViolationDetail(Integer violationId, ViolationDetail detail) {
        Violation violation = violationRepository.findById(violationId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + violationId));
        detail.setViolation(violation);
        if (detail.getViolationType() != null && detail.getViolationType().getId() != null) {
            ViolationType violationType = violationTypeRepository.findById(detail.getViolationType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + detail.getViolationType().getId()));
            detail.setViolationType(violationType);
        }
        return violationDetailRepository.save(detail);
    }

    // Cập nhật chi tiết vi phạm
    public ViolationDetail updateViolationDetail(Integer detailId, ViolationDetail updatedDetail) {
        ViolationDetail existingDetail = violationDetailRepository.findById(detailId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId));

        if (updatedDetail.getViolationType() != null && updatedDetail.getViolationType().getId() != null) {
            ViolationType violationType = violationTypeRepository.findById(updatedDetail.getViolationType().getId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + updatedDetail.getViolationType().getId()));
            existingDetail.setViolationType(violationType);
        }
        if (updatedDetail.getImageUrl() != null) {
            existingDetail.setImageUrl(updatedDetail.getImageUrl());
        }
        if (updatedDetail.getVideoUrl() != null) {
            existingDetail.setVideoUrl(updatedDetail.getVideoUrl());
        }
        if (updatedDetail.getLocation() != null) {
            existingDetail.setLocation(updatedDetail.getLocation());
        }
        if (updatedDetail.getViolationTime() != null) {
            existingDetail.setViolationTime(updatedDetail.getViolationTime());
        }
        if (updatedDetail.getSpeed() != null) {
            existingDetail.setSpeed(updatedDetail.getSpeed());
        }
        if (updatedDetail.getAdditionalNotes() != null) {
            existingDetail.setAdditionalNotes(updatedDetail.getAdditionalNotes());
        }
        return violationDetailRepository.save(existingDetail);
    }

    // Xóa chi tiết vi phạm
    public void deleteViolationDetail(Integer detailId) {
        if (!violationDetailRepository.existsById(detailId.longValue())) {
            throw new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId);
        }
        violationDetailRepository.deleteById(detailId.longValue());
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
    public ViolationType updateViolationType(Integer id, ViolationType updatedType) {
        ViolationType existingType = violationTypeRepository.findById(id.longValue())
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