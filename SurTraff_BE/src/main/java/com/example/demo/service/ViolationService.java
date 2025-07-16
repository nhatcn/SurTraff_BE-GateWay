package com.example.demo.service;

import com.example.demo.DTO.ViolationDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository,
                            ViolationTypeRepository violationTypeRepository, VehicleTypeRepository vehicleTypeRepository,
                            VehicleRepository vehicleRepository, ViolationDetailRepository violationDetailRepository,
                            UserRepository userRepository) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
        this.violationTypeRepository = violationTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.violationDetailRepository = violationDetailRepository;
        this.userRepository = userRepository;
    }

    public List<ViolationsDTO> getAllViolations() {
        return violationRepository.findAll().stream()
                .map(v -> {
                    ViolationsDTO dto = new ViolationsDTO();
                    dto.setId(v.getId());
                    dto.setCameraId(v.getCamera() != null ? v.getCamera().getId() : null);
                    dto.setVehicleTypeId(v.getVehicleType() != null ? v.getVehicleType().getId() : null);
                    dto.setVehicleId(v.getVehicle() != null ? v.getVehicle().getId() : null);
                    dto.setCreatedAt(v.getCreatedAt());
                    dto.setViolationDetails(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ViolationsDTO getViolationById(Long id) {
        Optional<Violation> violation = violationRepository.findById(id);
        return violation.map(this::toDTO).orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
    }

    public List<ViolationsDTO> getViolationHistory(String licensePlate) {
        return violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ViolationsDTO createViolation(ViolationsDTO dto) {
        Violation violation = toEntity(dto);
        // Lấy user từ vehicle nếu có
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()));
            violation.setVehicle(vehicle);
            if (vehicle.getUser() != null) {
                violation.getVehicle().setUser(vehicle.getUser()); // Đảm bảo user được liên kết
            }
        }
        violation = violationRepository.save(violation);
        return toDTO(violation);
    }

    public List<ViolationsDTO> getAllViolationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại với ID: " + userId));
        List<Vehicle> vehicles = vehicleRepository.findByUser(user);
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(v -> v.getVehicle() != null && vehicles.contains(v.getVehicle()))
                .collect(Collectors.toList());
        return violations.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ViolationsDTO updateViolation(Long id, ViolationsDTO dto) {
        Optional<Violation> optionalViolation = violationRepository.findById(id);
        if (optionalViolation.isPresent()) {
            Violation violation = optionalViolation.get();
            violation.setCamera(dto.getCameraId() != null ?
                    cameraRepository.findById(dto.getCameraId())
                            .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCameraId()))
                    : null);
            violation.setVehicleType(dto.getVehicleTypeId() != null ?
                    vehicleTypeRepository.findById(dto.getVehicleTypeId())
                            .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleTypeId()))
                    : null);
            if (dto.getVehicleId() != null) {
                Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                        .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()));
                violation.setVehicle(vehicle);
                if (vehicle.getUser() != null) {
                    vehicle.setUser(vehicle.getUser()); // Đảm bảo user được liên kết
                }
            }
            violation.setCreatedAt(dto.getCreatedAt());
            violation = violationRepository.save(violation);
            return toDTO(violation);
        }
        throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
    }

    public void deleteViolation(Long id) {
        if (!violationRepository.existsById(id)) {
            throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
        }
        violationRepository.deleteById(id);
    }

    public ViolationDetailDTO addViolationDetail(Long violationId, ViolationDetailDTO dto) {
        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + violationId));
        ViolationDetail detail = toDetailEntity(dto);
        detail.setViolation(violation);
        detail = violationDetailRepository.save(detail);
        return toDetailDTO(detail);
    }

    public ViolationDetailDTO updateViolationDetail(Long detailId, ViolationDetailDTO dto) {
        ViolationDetail existingDetail = violationDetailRepository.findById(detailId.intValue())
                .orElseThrow(() -> new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId));

        existingDetail.setViolationType(dto.getViolationTypeId() != null ?
                violationTypeRepository.findById(dto.getViolationTypeId())
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

    public void deleteViolationDetail(Long detailId) {
        if (!violationDetailRepository.existsById(detailId.intValue())) {
            throw new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId);
        }
        violationDetailRepository.deleteById(detailId.intValue());
    }

    public List<ViolationType> getAllViolationTypes() {
        return violationTypeRepository.findAll();
    }

    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll();
    }

    public ViolationType createViolationType(ViolationType violationType) {
        if (violationType.getTypeName() == null || violationType.getTypeName().isEmpty()) {
            throw new IllegalArgumentException("Tên loại vi phạm không được để trống");
        }
        return violationTypeRepository.save(violationType);
    }

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
                vehicleTypeRepository.findById(dto.getVehicleTypeId())
                        .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleTypeId()))
                : null);
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()));
            violation.setVehicle(vehicle);
        }
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
        dto.setViolationTypeId(detail.getViolationType() != null ? detail.getViolationType().getId().longValue() : null);
        dto.setImageUrl(detail.getImageUrl());
        dto.setVideoUrl(detail.getVideoUrl());
        dto.setLocation(detail.getLocation());
        dto.setViolationTime(detail.getViolationTime());
        dto.setSpeed(detail.getSpeed());
        dto.setAdditionalNotes(detail.getAdditionalNotes());
        dto.setCreatedAt(detail.getCreatedAt());
        return dto;
    }

    private ViolationDetail toDetailEntity(ViolationDetailDTO dto) {
        ViolationDetail detail = new ViolationDetail();
        detail.setId(dto.getId());
        detail.setViolationType(dto.getViolationTypeId() != null ?
                violationTypeRepository.findById(dto.getViolationTypeId())
                        .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()))
                : null);
        detail.setImageUrl(dto.getImageUrl());
        detail.setVideoUrl(dto.getVideoUrl());
        detail.setLocation(dto.getLocation());
        detail.setViolationTime(dto.getViolationTime() != null ? dto.getViolationTime() : null);
        detail.setSpeed(dto.getSpeed());
        detail.setAdditionalNotes(dto.getAdditionalNotes());
        detail.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : null);
        return detail;
    }

    public List<ViolationDTO> getViolationsByLicensePlate(String licensePlate) {
        Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicle.isEmpty()) {
            throw new EntityNotFoundException("Xe không tồn tại với biển số: " + licensePlate);
        }

        List<Violation> violations = violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate);
        return violations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ViolationDTO convertToDTO(Violation violation) {
        if (violation == null) return null;

        ViolationDTO dto = new ViolationDTO();
        dto.setId(violation.getId() != null ? violation.getId() : null);
        dto.setCamera_id(violation.getCamera() != null && violation.getCamera().getId() != null
                ? violation.getCamera().getId() : null);
        dto.setVehicle_type_id(violation.getVehicleType() != null && violation.getVehicleType().getId() != null
                ? violation.getVehicleType().getId() : null);
        dto.setVehicle_id(violation.getVehicle() != null && violation.getVehicle().getId() != null
                ? violation.getVehicle().getId() : null);
        dto.setCreated_at(violation.getCreatedAt());

        if (violation.getVehicle() != null) {
            dto.setLicensePlate(violation.getVehicle().getLicensePlate());
        }

        dto.setLocation(violation.getViolationDetails() != null && !violation.getViolationDetails().isEmpty()
                ? violation.getViolationDetails().get(0).getLocation() : null);

        return dto;
    }
}