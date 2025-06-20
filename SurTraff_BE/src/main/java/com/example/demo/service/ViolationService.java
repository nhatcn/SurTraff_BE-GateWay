package com.example.demo.service;

import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ViolationsDTO> getAllViolations() {
        return violationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Lấy vi phạm theo ID
    public ViolationsDTO getViolationById(Integer id) {
        Optional<Violation> violation = violationRepository.findById(id.longValue());
        return violation.map(this::toDTO).orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
    }

    // Lấy lịch sử vi phạm theo biển số
    public List<ViolationsDTO> getViolationHistory(String licensePlate) {
        return violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Tạo vi phạm mới
    public ViolationsDTO createViolation(ViolationsDTO dto) {
        Violation violation = toEntity(dto);
        violation = violationRepository.save(violation);
        return toDTO(violation);
    }

    // Cập nhật vi phạm
    public ViolationsDTO updateViolation(Integer id, ViolationsDTO dto) {
        Optional<Violation> optionalViolation = violationRepository.findById(id.longValue());
        if (optionalViolation.isPresent()) {
            Violation violation = optionalViolation.get();
            violation.setCamera(dto.getCameraId() != null ?
                    cameraRepository.findById(dto.getCameraId())
                            .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCameraId()))
                    : null);
            violation.setVehicleType(dto.getVehicleTypeId() != null ?
                    vehicleTypeRepository.findById(dto.getVehicleTypeId().longValue())
                            .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleTypeId()))
                    : null);
            violation.setVehicle(dto.getVehicleId() != null ?
                    vehicleRepository.findById(dto.getVehicleId().longValue())
                            .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()))
                    : null);
            violation.setCreatedAt(dto.getCreatedAt());
            violation = violationRepository.save(violation);
            return toDTO(violation);
        }
        throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
    }

    // Xóa vi phạm
    public void deleteViolation(Integer id) {
        if (!violationRepository.existsById(id.longValue())) {
            throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
        }
        violationRepository.deleteById(id.longValue());
    }

    // Thêm chi tiết vi phạm
    public ViolationDetailDTO addViolationDetail(Integer violationId, ViolationDetailDTO dto) {
        Violation violation = violationRepository.findById(violationId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + violationId));
        ViolationDetail detail = toDetailEntity(dto);
        detail.setViolation(violation);
        detail = violationDetailRepository.save(detail);
        return toDetailDTO(detail);
    }

    // Cập nhật chi tiết vi phạm
    public ViolationDetailDTO updateViolationDetail(Integer detailId, ViolationDetailDTO dto) {
        ViolationDetail existingDetail = violationDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId));

        existingDetail.setViolationType(dto.getViolationTypeId() != null ?
                violationTypeRepository.findById(dto.getViolationTypeId().longValue())
                        .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()))
                : null);
        existingDetail.setImageUrl(dto.getImageUrl());
        existingDetail.setVideoUrl(dto.getVideoUrl());
        existingDetail.setLocation(dto.getLocation());
        existingDetail.setViolationTime(dto.getViolationTime());
        existingDetail.setSpeed(dto.getSpeed());
        existingDetail.setAdditionalNotes(dto.getAdditionalNotes());

        existingDetail = violationDetailRepository.save(existingDetail);
        return toDetailDTO(existingDetail);
    }

    // Xóa chi tiết vi phạm
    public void deleteViolationDetail(Integer detailId) {
        if (!violationDetailRepository.existsById(detailId)) {
            throw new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId);
        }
        violationDetailRepository.deleteById(detailId);
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

    // Helper methods
    private ViolationsDTO toDTO(Violation violation) {
        ViolationsDTO dto = new ViolationsDTO();
        dto.setId(violation.getId());
        dto.setCameraId(violation.getCamera() != null ? violation.getCamera().getId() : null);
        dto.setVehicleTypeId(violation.getVehicleType() != null ? violation.getVehicleType().getId() : null);
        dto.setVehicleId(violation.getVehicle() != null ? violation.getVehicle().getId() : null);
        dto.setCreatedAt(violation.getCreatedAt());
        dto.setViolationDetails(violation.getViolationDetails() != null ?
                violation.getViolationDetails().stream().map(this::toDetailDTO).collect(Collectors.toList()) : null);
        return dto;
    }

    private Violation toEntity(ViolationsDTO dto) {
        Violation violation = new Violation();
        violation.setId(dto.getId());
        violation.setCamera(dto.getCameraId() != null ?
                cameraRepository.findById(dto.getCameraId())
                        .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCameraId()))
                : null);
        violation.setVehicleType(dto.getVehicleTypeId() != null ?
                vehicleTypeRepository.findById(dto.getVehicleTypeId().longValue())
                        .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleTypeId()))
                : null);
        violation.setVehicle(dto.getVehicleId() != null ?
                vehicleRepository.findById(dto.getVehicleId().longValue())
                        .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()))
                : null);
        violation.setCreatedAt(dto.getCreatedAt());
        if (dto.getViolationDetails() != null) {
            List<ViolationDetail> details = dto.getViolationDetails().stream()
                    .map(this::toDetailEntity)
                    .peek(detail -> detail.setViolation(violation))
                    .collect(Collectors.toList());
            violation.setViolationDetails(details);
        }
        return violation;
    }

    private ViolationDetailDTO toDetailDTO(ViolationDetail detail) {
        ViolationDetailDTO dto = new ViolationDetailDTO();
        dto.setId(detail.getId());
        dto.setViolationId(detail.getViolation() != null ? detail.getViolation().getId() : null);
        dto.setViolationTypeId(detail.getViolationType() != null ? detail.getViolationType().getId() : null);
        dto.setImageUrl(detail.getImageUrl());
        dto.setVideoUrl(detail.getVideoUrl());
        dto.setLocation(detail.getLocation());
        dto.setViolationTime(detail.getViolationTime().toString());
        dto.setSpeed(detail.getSpeed());
        dto.setAdditionalNotes(detail.getAdditionalNotes());
        return dto;
    }

    private ViolationDetail toDetailEntity(ViolationDetailDTO dto) {
        ViolationDetail detail = new ViolationDetail();
        detail.setId(dto.getId());
        detail.setViolationType(dto.getViolationTypeId() != null ?
                violationTypeRepository.findById(dto.getViolationTypeId().longValue())
                        .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()))
                : null);
        detail.setImageUrl(dto.getImageUrl());
        detail.setVideoUrl(dto.getVideoUrl());
        detail.setLocation(dto.getLocation());
        detail.setViolationTime(dto.getViolationTime());
        detail.setSpeed(dto.getSpeed());
        detail.setAdditionalNotes(dto.getAdditionalNotes());
        return detail;
    }
}