package com.example.demo.service;

import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;
    private final ViolationTypeRepository violationTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final ViolationDetailRepository violationDetailRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService; // Thêm CloudinaryService

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository,
                            ViolationTypeRepository violationTypeRepository, VehicleTypeRepository vehicleTypeRepository,
                            VehicleRepository vehicleRepository, ViolationDetailRepository violationDetailRepository,
                            UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
        this.violationTypeRepository = violationTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.violationDetailRepository = violationDetailRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService; // Khởi tạo CloudinaryService
    }

    @Transactional(readOnly = true)
    public List<ViolationsDTO> getAllViolations() {
        return violationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ViolationsDTO getViolationById(Long id) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
        Hibernate.initialize(violation.getViolationDetails());
        return toDTO(violation);
    }

    @Transactional(readOnly = true)
    public List<ViolationsDTO> getViolationHistory(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống");
        }
        return violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ViolationsDTO createViolation(ViolationsDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu vi phạm không được null");
        }
        Violation violation = new Violation();
        violation.setId(dto.getId());
        violation.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        violation.setStatus(dto.getStatus()); // Gán status từ DTO

        if (dto.getCameraId() != null) {
            Camera camera = cameraRepository.findById(dto.getCameraId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCameraId()));
            violation.setCamera(camera);
        }
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()));
            violation.setVehicle(vehicle);
        }
        if (dto.getVehicleType() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleType().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }

        Violation savedViolation = violationRepository.save(violation);

        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            List<ViolationDetail> details = dto.getViolationDetails().stream()
                    .map(this::toDetailEntity)
                    .peek(detail -> detail.setViolation(savedViolation))
                    .collect(Collectors.toList());
            violationDetailRepository.saveAll(details);
            savedViolation.setViolationDetails(details);
        }

        return toDTO(savedViolation);
    }

    @Transactional(readOnly = true)
    public List<ViolationsDTO> getAllViolationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại với ID: " + userId));
        List<Vehicle> vehicles = vehicleRepository.findByUserId(user.getId());
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(v -> v.getVehicle() != null && vehicles.contains(v.getVehicle()))
                .collect(Collectors.toList());
        return violations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ViolationsDTO updateViolation(Long id, ViolationsDTO dto) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));

        if (dto.getCameraId() != null) {
            Camera camera = cameraRepository.findById(dto.getCameraId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCameraId()));
            violation.setCamera(camera);
        }
        if (dto.getVehicleType() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleType().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicleId()));
            violation.setVehicle(vehicle);
        }
        if (dto.getCreatedAt() != null) {
            violation.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getStatus() != null) { // Cập nhật status nếu có
            violation.setStatus(dto.getStatus());
        }

        Violation savedViolation = violationRepository.save(violation);

        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            violationDetailRepository.deleteByViolationId(savedViolation.getId());
            List<ViolationDetail> details = dto.getViolationDetails().stream()
                    .map(this::toDetailEntity)
                    .peek(detail -> detail.setViolation(savedViolation))
                    .collect(Collectors.toList());
            violationDetailRepository.saveAll(details);
            savedViolation.setViolationDetails(details);
        }

        return toDTO(savedViolation);
    }

    public void deleteViolation(Long id) {
        if (!violationRepository.existsById(id)) {
            throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
        }
        violationRepository.deleteById(id);
    }

    public ViolationDetailDTO addViolationDetail(Long violationId, ViolationDetailDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết vi phạm không được null");
        }
        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + violationId));

        // Upload image nếu có
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(imageFile);
        }

        // Upload video nếu có
        String videoUrl = null;
        if (videoFile != null && !videoFile.isEmpty()) {
            videoUrl = cloudinaryService.uploadVideo(videoFile);
        }

        ViolationDetail detail = toDetailEntity(dto);
        detail.setViolation(violation);
        detail.setImageUrl(imageUrl != null ? imageUrl : dto.getImageUrl());
        detail.setVideoUrl(videoUrl != null ? videoUrl : dto.getVideoUrl());
        ViolationDetail savedDetail = violationDetailRepository.save(detail);
        return toDetailDTO(savedDetail);
    }

    public ViolationDetailDTO updateViolationDetail(Long detailId, ViolationDetailDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết vi phạm không được null");
        }
        ViolationDetail existingDetail = violationDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId));

        // Upload image nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            existingDetail.setImageUrl(imageUrl);
        } else if (dto.getImageUrl() != null) {
            existingDetail.setImageUrl(dto.getImageUrl());
        }

        // Upload video nếu có
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadVideo(videoFile);
            existingDetail.setVideoUrl(videoUrl);
        } else if (dto.getVideoUrl() != null) {
            existingDetail.setVideoUrl(dto.getVideoUrl());
        }

        if (dto.getViolationTypeId() != null) {
            ViolationType violationType = violationTypeRepository.findById(dto.getViolationTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()));
            existingDetail.setViolationType(violationType);
        }
        if (dto.getLocation() != null) existingDetail.setLocation(dto.getLocation());
        if (dto.getViolationTime() != null) existingDetail.setViolationTime(dto.getViolationTime());
        if (dto.getSpeed() != null) existingDetail.setSpeed(dto.getSpeed());
        if (dto.getAdditionalNotes() != null) existingDetail.setAdditionalNotes(dto.getAdditionalNotes());
        if (dto.getCreatedAt() != null) existingDetail.setCreatedAt(dto.getCreatedAt());

        ViolationDetail savedDetail = violationDetailRepository.save(existingDetail);
        return toDetailDTO(savedDetail);
    }

    public void deleteViolationDetail(Long detailId) {
        if (!violationDetailRepository.existsById(detailId)) {
            throw new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId);
        }
        violationDetailRepository.deleteById(detailId);
    }

    @Transactional(readOnly = true)
    public List<ViolationType> getAllViolationTypes() {
        return violationTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<VehicleType> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll();
    }

    public ViolationType createViolationType(ViolationType violationType) {
        if (violationType == null || violationType.getTypeName() == null || violationType.getTypeName().isEmpty()) {
            throw new IllegalArgumentException("Tên loại vi phạm không được để trống");
        }
        return violationTypeRepository.save(violationType);
    }

    public ViolationType updateViolationType(Long id, ViolationType updatedType) {
        if (updatedType == null) {
            throw new IllegalArgumentException("Dữ liệu loại vi phạm không được null");
        }
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
        if (violation == null) return null;
        ViolationsDTO dto = new ViolationsDTO();
        dto.setId(violation.getId());
        dto.setCameraId(violation.getCamera() != null ? violation.getCamera().getId() : null);
        dto.setVehicleType(violation.getVehicleType());
        dto.setVehicleId(violation.getVehicle() != null ? violation.getVehicle().getId() : null);
        dto.setCreatedAt(violation.getCreatedAt());
        if (Hibernate.isInitialized(violation.getViolationDetails()) && violation.getViolationDetails() != null) {
            dto.setViolationDetails(violation.getViolationDetails().stream()
                    .map(this::toDetailDTO)
                    .collect(Collectors.toList()));
        }
        dto.setStatus(violation.getStatus()); // Ánh xạ cột status
        return dto;
    }

    private Violation toEntity(ViolationsDTO dto) {
        if (dto == null) return null;
        Violation violation = new Violation();
        violation.setId(dto.getId());
        violation.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        violation.setStatus(dto.getStatus()); // Ánh xạ status từ DTO
        return violation;
    }

    private ViolationDetailDTO toDetailDTO(ViolationDetail detail) {
        if (detail == null) return null;
        ViolationDetailDTO dto = new ViolationDetailDTO();
        dto.setId(detail.getId());
        dto.setViolationId(detail.getViolation() != null ? detail.getViolation().getId() : null);
        dto.setViolationType(detail.getViolationType());
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
        if (dto == null) return null;
        ViolationDetail detail = new ViolationDetail();
        detail.setId(dto.getId());
        if (dto.getViolationTypeId() != null) {
            ViolationType violationType = violationTypeRepository.findById(dto.getViolationTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()));
            detail.setViolationType(violationType);
        }
        detail.setImageUrl(dto.getImageUrl());
        detail.setVideoUrl(dto.getVideoUrl());
        detail.setLocation(dto.getLocation());
        detail.setViolationTime(dto.getViolationTime());
        detail.setSpeed(dto.getSpeed());
        detail.setAdditionalNotes(dto.getAdditionalNotes());
        detail.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        return detail;
    }

    @Transactional(readOnly = true)
    public List<ViolationsDTO> getViolationsByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống");
        }
        Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(licensePlate);
        if (vehicle.isEmpty()) {
            throw new EntityNotFoundException("Xe không tồn tại với biển số: " + licensePlate);
        }
        List<Violation> violations = violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate);
        return violations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}