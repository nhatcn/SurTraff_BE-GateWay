package com.example.demo.service;

import com.example.demo.DTO.CameraDTO;
import com.example.demo.DTO.VehicleDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private final ZoneRepository zoneRepository;
    private final CloudinaryService cloudinaryService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository,
                            ViolationTypeRepository violationTypeRepository, VehicleTypeRepository vehicleTypeRepository,
                            VehicleRepository vehicleRepository, ViolationDetailRepository violationDetailRepository,
                            UserRepository userRepository, ZoneRepository zoneRepository, CloudinaryService cloudinaryService) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
        this.violationTypeRepository = violationTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.violationDetailRepository = violationDetailRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // Hàm toEntity cho CameraDTO
    private Camera toEntity(CameraDTO dto) {
        if (dto == null) return null;
        Camera camera = new Camera();
        camera.setId(dto.getId());
        camera.setName(dto.getName());
        camera.setLocation(dto.getLocation());
        camera.setStreamUrl(dto.getStreamUrl());
        camera.setThumbnail(dto.getThumbnail());
        camera.setLatitude(dto.getLatitude());
        camera.setLongitude(dto.getLongitude());

        // Xử lý zones từ zoneId
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepository.findById(dto.getZoneId())
                    .orElseThrow(() -> new EntityNotFoundException("Zone không tồn tại với ID: " + dto.getZoneId()));
            camera.setZones(List.of(zone));
        } else {
            camera.setZones(new ArrayList<>());
        }

        return camera;
    }

    public ViolationsDTO createViolation(ViolationsDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu vi phạm không được null");
        }

        Violation violation = new Violation();
        violation.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        violation.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");

        // Xử lý Camera
        if (dto.getCamera() != null && dto.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(dto.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCamera().getId()));
            violation.setCamera(camera);
        } else if (dto.getCamera() != null) {
            violation.setCamera(toEntity(dto.getCamera()));
        }

        // Xử lý Vehicle và licensePlate
        final String licensePlate; // Khai báo final để đảm bảo effectively final
        if (dto.getVehicle() != null && dto.getVehicle().getLicensePlate() != null && !dto.getVehicle().getLicensePlate().trim().isEmpty()) {
            licensePlate = dto.getVehicle().getLicensePlate();
            Optional<Vehicle> vehicleOpt = vehicleRepository.findByLicensePlate(licensePlate);
            if (vehicleOpt.isPresent()) {
                violation.setVehicle(vehicleOpt.get());
                if (dto.getVehicleType() == null && vehicleOpt.get().getVehicleType() != null) {
                    violation.setVehicleType(vehicleOpt.get().getVehicleType());
                }
            }
        } else if (dto.getVehicle() != null && dto.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicle().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicle().getId()));
            violation.setVehicle(vehicle);
            licensePlate = vehicle.getLicensePlate(); // Gán licensePlate từ Vehicle
        } else {
            throw new IllegalArgumentException("Phải cung cấp ít nhất một trong hai: licensePlate hoặc vehicle.id");
        }

        // Xử lý VehicleType
        if (dto.getVehicleType() != null && dto.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleType().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }

        // Lưu Violation
        Violation savedViolation = violationRepository.save(violation);

        // Xử lý ViolationDetail và file ảnh/video
        List<ViolationDetail> details = new ArrayList<>();
        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            details = dto.getViolationDetails().stream()
                    .map(detailDTO -> {
                        ViolationDetail detail = toDetailEntity(detailDTO);
                        detail.setViolation(savedViolation);
                        detail.setLicensePlate(licensePlate); // Lưu licensePlate vào ViolationDetail
                        if (detailDTO.getViolationTypeId() != null) {
                            ViolationType violationType = violationTypeRepository.findById(detailDTO.getViolationTypeId())
                                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + detailDTO.getViolationTypeId()));
                            detail.setViolationType(violationType);
                        }
                        return detail;
                    })
                    .collect(Collectors.toList());
        }

        // Nếu có file ảnh hoặc video, tạo một ViolationDetail mới
        if (imageFile != null || videoFile != null) {
            ViolationDetailDTO detailDTO = new ViolationDetailDTO();
            detailDTO.setViolationTime(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
            detailDTO.setLocation(dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()
                    ? dto.getViolationDetails().get(0).getLocation() : null);
            detailDTO.setLicensePlate(licensePlate); // Lưu licensePlate vào ViolationDetailDTO
            ViolationDetailDTO savedDetailDTO = addViolationDetail(savedViolation.getId(), detailDTO, imageFile, videoFile);
            ViolationDetail detail = toDetailEntity(savedDetailDTO);
            detail.setViolation(savedViolation);
            detail.setLicensePlate(licensePlate); // Đảm bảo licensePlate được lưu
            details.add(detail);
        }

        if (!details.isEmpty()) {
            violationDetailRepository.saveAll(details);
            savedViolation.setViolationDetails(details);
        }

        return toDTO(savedViolation);
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
    public ViolationDetailDTO addViolationDetail(Long violationId, ViolationDetailDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết vi phạm không được null");
        }
        Violation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + violationId));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(imageFile);
        } else if (dto.getImageUrl() != null) {
            imageUrl = dto.getImageUrl();
        }

        String videoUrl = null;
        if (videoFile != null && !videoFile.isEmpty()) {
            videoUrl = cloudinaryService.uploadVideo(videoFile);
        } else if (dto.getVideoUrl() != null) {
            videoUrl = dto.getVideoUrl();
        }

        ViolationDetail detail = toDetailEntity(dto);
        detail.setViolation(violation);
        detail.setImageUrl(imageUrl);
        detail.setVideoUrl(videoUrl);
        detail.setLicensePlate(dto.getLicensePlate());

        if (dto.getViolationTypeId() != null) {
            ViolationType violationType = violationTypeRepository.findById(dto.getViolationTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()));
            detail.setViolationType(violationType);
        }

        ViolationDetail savedDetail = violationDetailRepository.save(detail);
        return toDetailDTO(savedDetail);
    }
    public ViolationsDTO updateViolation(Long id, ViolationsDTO dto) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));

        if (dto.getCamera() != null && dto.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(dto.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCamera().getId()));
            violation.setCamera(camera);
        }
        if (dto.getVehicleType() != null && dto.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleType().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }
        if (dto.getVehicle() != null && dto.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicle().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Xe không tồn tại với ID: " + dto.getVehicle().getId()));
            violation.setVehicle(vehicle);
        }
        if (dto.getCreatedAt() != null) {
            violation.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getStatus() != null) {
            violation.setStatus(dto.getStatus());
        }

        // Xử lý ViolationDetail
        String licensePlate = (dto.getVehicle() != null && dto.getVehicle().getLicensePlate() != null)
                ? dto.getVehicle().getLicensePlate() : null;
        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            violationDetailRepository.deleteByViolationId(violation.getId());
            List<ViolationDetail> details = dto.getViolationDetails().stream()
                    .map(detailDTO -> {
                        ViolationDetail detail = toDetailEntity(detailDTO);
                        detail.setViolation(violation);
                        detail.setLicensePlate(licensePlate); // Lưu licensePlate vào ViolationDetail
                        if (detailDTO.getViolationTypeId() != null) {
                            ViolationType violationType = violationTypeRepository.findById(detailDTO.getViolationTypeId())
                                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + detailDTO.getViolationTypeId()));
                            detail.setViolationType(violationType);
                        }
                        return detail;
                    })
                    .collect(Collectors.toList());
            violationDetailRepository.saveAll(details);
            violation.setViolationDetails(details);
        }

        Violation savedViolation = violationRepository.save(violation);
        return toDTO(savedViolation);
    }

    public void deleteViolation(Long id) {
        if (!violationRepository.existsById(id)) {
            throw new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id);
        }
        violationRepository.deleteById(id);
    }


    @Transactional
    public ViolationsDTO createViolationNhat(ViolationsDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {

        Violation violation = new Violation();
        violation.setCamera(cameraRepository.findById(dto.getCamera().getId()).orElseThrow());
        violation.setCreatedAt(dto.getCreatedAt());
        violation.setStatus(dto.getStatus());
        violation = violationRepository.save(violation);

        for (ViolationDetailDTO detailDTO : dto.getViolationDetails()) {
            ViolationDetail detail = new ViolationDetail();
            detail.setViolation(violation);
            detail.setLocation(dto.getCamera().getLocation());
            detail.setViolationTime(detailDTO.getViolationTime());
            detail.setViolationType(violationTypeRepository.findById(detailDTO.getViolationTypeId()).orElseThrow());


            if (imageFile != null && !imageFile.isEmpty()) {
                detail.setImageUrl(cloudinaryService.uploadImage(imageFile));
            }

            if (videoFile != null && !videoFile.isEmpty()) {

                detail.setVideoUrl(cloudinaryService.uploadVideo(videoFile));
            }

            violationDetailRepository.save(detail); // ❗ GỌI save ở đây
        }

        return  toDTO(violation);
    }

    public ViolationDetailDTO updateViolationDetail(Long detailId, ViolationDetailDTO dto, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết vi phạm không được null");
        }
        ViolationDetail existingDetail = violationDetailRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Chi tiết vi phạm không tồn tại với ID: " + detailId));

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            existingDetail.setImageUrl(imageUrl);
        } else if (dto.getImageUrl() != null) {
            existingDetail.setImageUrl(dto.getImageUrl());
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = cloudinaryService.uploadVideo(videoFile);
            existingDetail.setVideoUrl(videoUrl);
        } else if (dto.getVideoUrl() != null) {
            String videoUrl = dto.getVideoUrl();
            existingDetail.setVideoUrl(videoUrl);
        }

        if (dto.getLocation() != null) existingDetail.setLocation(dto.getLocation());
        if (dto.getViolationTime() != null) existingDetail.setViolationTime(dto.getViolationTime());
        if (dto.getSpeed() != null) existingDetail.setSpeed(dto.getSpeed());
        if (dto.getAdditionalNotes() != null) existingDetail.setAdditionalNotes(dto.getAdditionalNotes());
        if (dto.getLicensePlate() != null) existingDetail.setLicensePlate(dto.getLicensePlate()); // Cập nhật licensePlate

        if (dto.getViolationTypeId() != null) {
            ViolationType violationType = violationTypeRepository.findById(dto.getViolationTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + dto.getViolationTypeId()));
            existingDetail.setViolationType(violationType);
        }

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

    public ViolationsDTO updateViolationStatus(Long id, String status) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        List<String> validStatuses = Arrays.asList("PENDING", "REQUEST", "RESOLVED", "DISMISSED");
        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status + ". Trạng thái hợp lệ: " + validStatuses);
        }

        violation.setStatus(status.toUpperCase());
        Violation savedViolation = violationRepository.save(violation);
        Hibernate.initialize(savedViolation.getViolationDetails());
        return toDTO(savedViolation);
    }

    private ViolationsDTO toDTO(Violation violation) {
        if (violation == null) return null;
        ViolationsDTO dto = new ViolationsDTO();
        dto.setId(violation.getId());
        dto.setCamera(violation.getCamera() != null ? convertCameraToDTO(violation.getCamera()) : null);

        if (violation.getVehicleType() != null) {
            Hibernate.initialize(violation.getVehicleType());
            VehicleType vehicleType = violation.getVehicleType();
            VehicleType copy = new VehicleType();
            copy.setId(vehicleType.getId());
            copy.setTypeName(vehicleType.getTypeName());
            dto.setVehicleType(copy);
        } else {
            dto.setVehicleType(null);
        }

        if (violation.getVehicle() != null) {
            dto.setVehicle(convertVehicleToDTO(violation.getVehicle()));
        } else {
            // Nếu không có vehicle, lấy licensePlate từ ViolationDetail
            if (violation.getViolationDetails() != null && !violation.getViolationDetails().isEmpty()) {
                String licensePlate = violation.getViolationDetails().stream()
                        .filter(detail -> detail.getLicensePlate() != null)
                        .map(ViolationDetail::getLicensePlate)
                        .findFirst()
                        .orElse(null);
                if (licensePlate != null) {
                    VehicleDTO vehicleDTO = new VehicleDTO();
                    vehicleDTO.setLicensePlate(licensePlate);
                    dto.setVehicle(vehicleDTO);
                } else {
                    dto.setVehicle(null);
                }
            } else {
                dto.setVehicle(null);
            }
        }

        dto.setCreatedAt(violation.getCreatedAt());
        if (Hibernate.isInitialized(violation.getViolationDetails()) && violation.getViolationDetails() != null) {
            dto.setViolationDetails(violation.getViolationDetails().stream()
                    .map(this::toDetailDTO)
                    .collect(Collectors.toList()));
        }
        dto.setStatus(violation.getStatus());
        return dto;
    }

    private Violation toEntity(ViolationsDTO dto) {
        if (dto == null) return null;
        Violation violation = new Violation();
        violation.setId(dto.getId());
        violation.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        violation.setStatus(dto.getStatus());
        return violation;
    }

    private ViolationDetailDTO toDetailDTO(ViolationDetail detail) {
        if (detail == null) return null;
        ViolationDetailDTO dto = new ViolationDetailDTO();
        dto.setId(detail.getId());
        dto.setViolationId(detail.getViolation() != null ? detail.getViolation().getId() : null);
        dto.setViolationTypeId(detail.getViolationType() != null ? detail.getViolationType().getId().longValue() : null);
        dto.setViolationType(detail.getViolationType());
        dto.setImageUrl(detail.getImageUrl());
        dto.setVideoUrl(detail.getVideoUrl());
        dto.setLocation(detail.getLocation());
        dto.setViolationTime(detail.getViolationTime());
        dto.setSpeed(detail.getSpeed());
        dto.setAdditionalNotes(detail.getAdditionalNotes());
        dto.setCreatedAt(detail.getCreatedAt());
        dto.setLicensePlate(detail.getLicensePlate()); // Ánh xạ licensePlate
        return dto;
    }

    private ViolationDetail toDetailEntity(ViolationDetailDTO dto) {
        if (dto == null) return null;
        ViolationDetail detail = new ViolationDetail();
        detail.setId(dto.getId());
        detail.setImageUrl(dto.getImageUrl());
        detail.setVideoUrl(dto.getVideoUrl());
        detail.setLocation(dto.getLocation());
        detail.setViolationTime(dto.getViolationTime());
        detail.setSpeed(dto.getSpeed());
        detail.setAdditionalNotes(dto.getAdditionalNotes());
        detail.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        detail.setLicensePlate(dto.getLicensePlate()); // Ánh xạ licensePlate
        return detail;
    }

    private CameraDTO convertCameraToDTO(Camera camera) {
        return CameraDTO.builder()
                .id(camera.getId())
                .name(camera.getName())
                .location(camera.getLocation())
                .streamUrl(camera.getStreamUrl())
                .thumbnail(camera.getThumbnail())
                .zoneId(camera.getZones() != null && !camera.getZones().isEmpty() ? camera.getZones().get(0).getId() : null)
                .latitude(camera.getLatitude())
                .longitude(camera.getLongitude())
                .build();
    }

    private VehicleDTO convertVehicleToDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .licensePlate(vehicle.getLicensePlate())
                .userId(vehicle.getUser() != null ? vehicle.getUser().getId() : null)
                .vehicleTypeId(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getId() : null)
                .color(vehicle.getColor())
                .brand(vehicle.getBrand())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ViolationsDTO> getViolationsByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống");
        }
        // Sửa để hỗ trợ tìm bằng licensePlate trong ViolationDetail
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(v -> v.getViolationDetails() != null && v.getViolationDetails().stream()
                        .anyMatch(detail -> licensePlate.equals(detail.getLicensePlate())))
                .collect(Collectors.toList());
        if (violations.isEmpty()) {
            Optional<Vehicle> vehicle = vehicleRepository.findByLicensePlate(licensePlate);
            if (vehicle.isPresent()) {
                violations = violationRepository.findByVehicleLicensePlateOrderByCreatedAtDesc(licensePlate);
            }
        }
        return violations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}