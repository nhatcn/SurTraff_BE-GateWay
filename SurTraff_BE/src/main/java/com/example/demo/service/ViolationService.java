package com.example.demo.service;

import com.example.demo.DTO.CameraDTO;
import com.example.demo.DTO.VehicleDTO;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.DTO.ViolationsDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ViolationService {
    private static final Logger logger = LoggerFactory.getLogger(ViolationService.class);

    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;
    private final ViolationTypeRepository violationTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final ViolationDetailRepository violationDetailRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final NotificationsRepository notificationsRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final String pdfShiftApiKey = "sk_cba8b808d8d558b87e82ee38c26d5382b95de103";

    @Autowired
    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository,
                            ViolationTypeRepository violationTypeRepository, VehicleTypeRepository vehicleTypeRepository,
                            VehicleRepository vehicleRepository, ViolationDetailRepository violationDetailRepository,
                            UserRepository userRepository, ZoneRepository zoneRepository,
                            NotificationsRepository notificationsRepository, CloudinaryService cloudinaryService) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
        this.violationTypeRepository = violationTypeRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.violationDetailRepository = violationDetailRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.notificationsRepository = notificationsRepository;
        this.cloudinaryService = cloudinaryService;
    }

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

        if (dto.getCamera() != null && dto.getCamera().getId() != null) {
            Camera camera = cameraRepository.findById(dto.getCamera().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Camera không tồn tại với ID: " + dto.getCamera().getId()));
            violation.setCamera(camera);
        } else if (dto.getCamera() != null) {
            violation.setCamera(toEntity(dto.getCamera()));
        }

        final String licensePlate;
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
            licensePlate = vehicle.getLicensePlate();
        } else {
            throw new IllegalArgumentException("Phải cung cấp ít nhất một trong hai: licensePlate hoặc vehicle.id");
        }

        if (dto.getVehicleType() != null && dto.getVehicleType().getId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(dto.getVehicleType().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Loại xe không tồn tại với ID: " + dto.getVehicleType().getId()));
            violation.setVehicleType(vehicleType);
        }

        Violation savedViolation = violationRepository.save(violation);

        List<ViolationDetail> details = new ArrayList<>();
        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            details = dto.getViolationDetails().stream()
                    .map(detailDTO -> {
                        ViolationDetail detail = toDetailEntity(detailDTO);
                        detail.setViolation(savedViolation);
                        detail.setLicensePlate(licensePlate);
                        if (detailDTO.getViolationTypeId() != null) {
                            ViolationType violationType = violationTypeRepository.findById(detailDTO.getViolationTypeId())
                                    .orElseThrow(() -> new EntityNotFoundException("Loại vi phạm không tồn tại với ID: " + detailDTO.getViolationTypeId()));
                            detail.setViolationType(violationType);
                        }
                        return detail;
                    })
                    .collect(Collectors.toList());
        }

        if (imageFile != null || videoFile != null) {
            ViolationDetailDTO detailDTO = new ViolationDetailDTO();
            detailDTO.setViolationTime(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
            detailDTO.setLocation(dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()
                    ? dto.getViolationDetails().get(0).getLocation() : null);
            detailDTO.setLicensePlate(licensePlate);
            ViolationDetailDTO savedDetailDTO = addViolationDetail(savedViolation.getId(), detailDTO, imageFile, videoFile);
            ViolationDetail detail = toDetailEntity(savedDetailDTO);
            detail.setViolation(savedViolation);
            detail.setLicensePlate(licensePlate);
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

        String licensePlate = (dto.getVehicle() != null && dto.getVehicle().getLicensePlate() != null)
                ? dto.getVehicle().getLicensePlate() : null;
        if (dto.getViolationDetails() != null && !dto.getViolationDetails().isEmpty()) {
            violationDetailRepository.deleteByViolationId(violation.getId());
            List<ViolationDetail> details = dto.getViolationDetails().stream()
                    .map(detailDTO -> {
                        ViolationDetail detail = toDetailEntity(detailDTO);
                        detail.setViolation(violation);
                        detail.setLicensePlate(licensePlate);
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
        if(dto.getVehicle()!=null){
            violation.setVehicle(vehicleRepository.getReferenceById(dto.getVehicle().getId()));
        }
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
            if (dto.getViolationDetails() != null ) {
                detail.setLicensePlate(dto.getViolationDetails().get(0).getLicensePlate());
            }
            if (videoFile != null && !videoFile.isEmpty()) {
                detail.setVideoUrl(cloudinaryService.uploadVideo(videoFile));
            }

            violationDetailRepository.save(detail);
        }

        return toDTO(violation);
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
            existingDetail.setVideoUrl(dto.getVideoUrl());
        }

        if (dto.getLocation() != null) existingDetail.setLocation(dto.getLocation());
        if (dto.getViolationTime() != null) existingDetail.setViolationTime(dto.getViolationTime());
        if (dto.getSpeed() != null) existingDetail.setSpeed(dto.getSpeed());
        if (dto.getAdditionalNotes() != null) existingDetail.setAdditionalNotes(dto.getAdditionalNotes());
        if (dto.getLicensePlate() != null) existingDetail.setLicensePlate(dto.getLicensePlate());

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

    @Transactional
    public ViolationsDTO requestViolation(Long id) {
        logger.info("Requesting violation with ID: {}", id);
        Violation violation = findViolationById(id);
        violation.setStatus("REQUESTED");
        Violation savedViolation = violationRepository.save(violation);
        Hibernate.initialize(savedViolation.getViolationDetails());
        logger.info("Violation ID: {} status updated to REQUESTED", id);
        return toDTO(savedViolation);
    }

    @Transactional
    public ViolationsDTO processViolation(Long id) {
        logger.info("Processing violation with ID: {}", id);
        Violation violation = findViolationById(id);
        violation.setStatus("PROCESSED");
        Violation savedViolation = violationRepository.save(violation);
        Hibernate.initialize(savedViolation.getViolationDetails());
        logger.info("Violation ID: {} status updated to PROCESSED", id);

        // Thêm logic để thông báo cho người dùng rằng chi tiết vi phạm có thể được xem
        try {
            User user = savedViolation.getVehicle().getUser();
            Vehicle vehicle = savedViolation.getVehicle();
            String violationType = savedViolation.getViolationDetails() != null && !savedViolation.getViolationDetails().isEmpty()
                    ? savedViolation.getViolationDetails().get(0).getViolationType() != null
                    ? savedViolation.getViolationDetails().get(0).getViolationType().getTypeName() : "Not specified"
                    : "Not specified";
            String message = String.format(
                    "Your violation with ID %d for vehicle %s is now processed and available for review.",
                    savedViolation.getId(),
                    vehicle.getLicensePlate()
            );
            Notifications notification = Notifications.builder()
                    .user(user)
                    .vehicle(vehicle)
                    .accident(null)
                    .violation(savedViolation)
                    .message(message)
                    .notification_type("violation_processed")
                    .read(false)
                    .created_at(LocalDateTime.now())
                    .build();
            notificationsRepository.save(notification);
            logger.info("Notification saved for user: {} for violation ID: {}", user.getId(), id);
        } catch (Exception e) {
            logger.error("Failed to save notification for violation ID: {}", id, e);
        }

        return toDTO(savedViolation);
    }

    @Transactional
    public ViolationsDTO approveViolation(Long id) {
        logger.info("Approving violation with ID: {}", id);
        Violation violation = findViolationById(id);
        violation.setStatus("APPROVED");
        Violation savedViolation = violationRepository.save(violation);
        Hibernate.initialize(savedViolation.getViolationDetails());
        logger.info("Violation ID: {} status updated to APPROVED", id);

        try {
//            File pdfFile = generateViolationPDF(savedViolation);
            sendApprovalEmail(savedViolation);
//            Files.deleteIfExists(pdfFile.toPath());
            logger.info("Email sent and PDF deleted successfully for violation ID: {}", id);
        } catch ( MessagingException e) {
//            logger.error("Error generating PDF or sending email for violation ID: {}", id, e);
            throw new RuntimeException("Failed to send approval email or generate PDF: " + e.getMessage(), e);
        }

        try {
            User user = savedViolation.getVehicle().getUser();
            Vehicle vehicle = savedViolation.getVehicle();
            String violationType = savedViolation.getViolationDetails() != null && !savedViolation.getViolationDetails().isEmpty()
                    ? savedViolation.getViolationDetails().get(0).getViolationType() != null
                    ? savedViolation.getViolationDetails().get(0).getViolationType().getTypeName() : "Not specified"
                    : "Not specified";
            String message = String.format(
                    "Your vehicle %s was recorded for a %s violation at %s.",
                    vehicle.getLicensePlate(),
                    violationType,
                    savedViolation.getCamera() != null ? savedViolation.getCamera().getLocation() : "Not specified"
            );
            Notifications notification = Notifications.builder()
                    .user(user)
                    .vehicle(vehicle)
                    .accident(null)
                    .violation(savedViolation)
                    .message(message)
                    .notification_type("violation")
                    .read(false)
                    .created_at(LocalDateTime.now())
                    .build();
            notificationsRepository.save(notification);
            logger.info("Notification saved for user: {} for violation ID: {}", user.getId(), id);
        } catch (Exception e) {
            logger.error("Failed to save notification for violation ID: {}", id, e);
        }

        return toDTO(savedViolation);
    }

    @Transactional
    public ViolationsDTO rejectViolation(Long id) {
        logger.info("Rejecting violation with ID: {}", id);
        Violation violation = findViolationById(id);
        violation.setStatus("REJECTED");
        Violation savedViolation = violationRepository.save(violation);
        Hibernate.initialize(savedViolation.getViolationDetails());
        logger.info("Violation ID: {} status updated to REJECTED", id);
        return toDTO(savedViolation);
    }

    private Violation findViolationById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid violation ID: " + id);
        }
        return violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Violation not found with ID: " + id));
    }

    private File generateViolationPDF(Violation violation) throws IOException {
        logger.info("Starting PDF generation for violation ID: {}", violation.getId());
        File pdfFile = File.createTempFile("violation_" + violation.getId(), ".pdf");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDate = violation.getCreatedAt().format(formatter);
        String issuedDate = formatter.format(LocalDateTime.now());

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>")
                .append("<html><head>")
                .append("<meta charset='UTF-8'>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 40px; }")
                .append("h1 { color: #005555; text-align: center; }")
                .append("h2 { color: #007777; }")
                .append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                .append("th { background-color: #f2f2f2; font-weight: bold; }")
                .append("img { max-width: 300px; height: auto; margin: 10px 0; }")
                .append(".footer { text-align: center; font-size: 12px; color: #005555; }")
                .append("</style>")
                .append("</head><body>");

        htmlContent.append("<h1>Violation Report</h1>")
                .append("<p style='text-align: center;'>Issued Date: ").append(issuedDate).append("</p>");

        htmlContent.append("<h2>General Information</h2>")
                .append("<table>")
                .append("<tr><th>Violation ID</th><td>").append(violation.getId()).append("</td></tr>")
                .append("<tr><th>License Plate</th><td>").append(violation.getVehicle().getLicensePlate()).append("</td></tr>")
                .append("<tr><th>Owner</th><td>").append(violation.getVehicle().getUser().getFullName()).append("</td></tr>")
                .append("<tr><th>Vehicle Type</th><td>").append(violation.getVehicleType() != null ? violation.getVehicleType().getTypeName() : "Not specified").append("</td></tr>")
                .append("<tr><th>Camera Location</th><td>").append(violation.getCamera().getLocation()).append("</td></tr>")
                .append("<tr><th>Violation Date</th><td>").append(formattedDate).append("</td></tr>")
                .append("<tr><th>Status</th><td>Approved</td></tr>")
                .append("</table>");

        htmlContent.append("<h2>Violation Details</h2>");
        if (violation.getViolationDetails() != null && !violation.getViolationDetails().isEmpty()) {
            for (ViolationDetail detail : violation.getViolationDetails()) {
                String violationType = detail.getViolationType() != null ? detail.getViolationType().getTypeName() : "Not specified";
                String imageUrl = detail.getImageUrl() != null ? detail.getImageUrl() : "Not available";
                String videoUrl = detail.getVideoUrl() != null ? detail.getVideoUrl() : "Not available";
                String location = detail.getLocation() != null ? detail.getLocation() : violation.getCamera().getLocation();
                String violationTime = detail.getViolationTime() != null ? detail.getViolationTime().format(formatter) : formattedDate;
                String speed = detail.getSpeed() != null ? detail.getSpeed() + " km/h" : "Not specified";
                String additionalNotes = detail.getAdditionalNotes() != null ? detail.getAdditionalNotes() : "None";
                String createdAt = detail.getCreatedAt() != null ? detail.getCreatedAt().format(formatter) : "Not specified";
                String licensePlate = detail.getLicensePlate() != null ? detail.getLicensePlate() : violation.getVehicle().getLicensePlate();

                htmlContent.append("<table>")
                        .append("<tr><th>Detail ID</th><td>").append(detail.getId()).append("</td></tr>")
                        .append("<tr><th>Violation Type</th><td>").append(violationType).append("</td></tr>")
                        .append("<tr><th>Image URL</th><td>").append(imageUrl).append("</td></tr>");
                if (detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()) {
                    htmlContent.append("<tr><th>Image</th><td><img src='").append(detail.getImageUrl()).append("' alt='Violation Image'></td></tr>");
                }
                htmlContent.append("<tr><th>Video URL</th><td>").append(videoUrl).append("</td></tr>")
                        .append("<tr><th>Location</th><td>").append(location).append("</td></tr>")
                        .append("<tr><th>Violation Time</th><td>").append(violationTime).append("</td></tr>")
                        .append("<tr><th>Speed</th><td>").append(speed).append("</td></tr>")
                        .append("<tr><th>Additional Notes</th><td>").append(additionalNotes).append("</td></tr>")
                        .append("<tr><th>Created At</th><td>").append(createdAt).append("</td></tr>")
                        .append("<tr><th>License Plate</th><td>").append(licensePlate).append("</td></tr>")
                        .append("</table>");
            }
        } else {
            htmlContent.append("<p>No violation details available</p>");
        }

        htmlContent.append("<h2>Contact Information</h2>")
                .append("<table>")
                .append("<tr><th>Email</th><td>support@violationmanagement.com</td></tr>")
                .append("<tr><th>Phone</th><td>+84-123-456-789</td></tr>")
                .append("<tr><th>Website</th><td>www.violationmanagement.com</td></tr>")
                .append("</table>")
                .append("<p>Please contact our support team within 7 days for any inquiries or complaints.</p>");

        htmlContent.append("<p class='footer'>Violation Management System -- Committed to Road Safety and Compliance</p>")
                .append("</body></html>");

        try {
            URL url = new URL("https://api.pdfshift.io/v3/convert/pdf");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(("api:" + pdfShiftApiKey).getBytes(StandardCharsets.UTF_8)));
            connection.setDoOutput(true);

            String jsonInput = "{\"source\": \"" + htmlContent.toString().replace("\n", "").replace("\"", "\\\"") + "\", \"sandbox\": false}";
            try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                connection.getOutputStream().write(jsonInput.getBytes(StandardCharsets.UTF_8));
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream()) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    logger.info("PDF generated successfully for violation ID: {}", violation.getId());
                } else {
                    logger.error("PDFShift API call failed with response code: {}", responseCode);
                    throw new IOException("PDFShift API call failed with response code: " + responseCode);
                }
            } finally {
                connection.disconnect();
            }
            return pdfFile;
        } catch (IOException e) {
            logger.error("Error generating PDF for violation ID: {}: {}", violation.getId(), e.getMessage(), e);
            throw new IOException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    private void sendApprovalEmail(Violation accident) throws MessagingException {
        String userEmail = accident.getVehicle().getUser().getEmail();
        String fullName = accident.getVehicle().getUser().getFullName();
        String licensePlate = accident.getVehicle().getLicensePlate();
        String location = accident.getCamera().getLocation();
        String subject = "Notification: Your violation has been recorded";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userEmail);
        helper.setSubject(subject);

        String imageCid = "violationImage001";
        String htmlContent = String.format(
                "<p>Dear %s,</p>" +
                        "<p>We have recorded a violation related to your vehicle:</p>" +
                        "<ul>" +
                        "<li><strong>Violation ID:</strong> %d</li>" +
                        "<li><strong>License Plate:</strong> %s</li>" +
                        "<li><strong>Location:</strong> %s</li>" +
                        "<li><strong>Status:</strong> Approved</li>" +
                        "</ul>" +
                        "<p><strong>Violation Image:</strong></p>" +
                        "<img src='cid:%s' width='500'/>" +
                        "<p>If you have questions, contact support.</p>" +
                        "<p>Regards,<br>Accident Management System</p>",
                fullName, accident.getId(), licensePlate, location, imageCid
        );
        helper.setText(htmlContent, true);

        String imageUrl = accident.getViolationDetails().get(0).getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try (InputStream in = new URL(imageUrl).openStream()) {
                byte[] imageBytes = in.readAllBytes();
                ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
                helper.addInline(imageCid, imageResource, "image/jpeg");
                logger.info("Attached image from URL: {}", imageUrl);
            } catch (Exception e) {
                logger.warn("Failed to load image from URL: {}. Error: {}", imageUrl, e.getMessage());
            }
        } else {
            logger.warn("No image URL available for accident ID: {}", accident.getId());
        }

        mailSender.send(message);
    }

    private ViolationsDTO toDTO(Violation violation) {
        if (violation == null) return null;
        ViolationsDTO dto = new ViolationsDTO();
        dto.setId(violation.getId().intValue());
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
        violation.setId(dto.getId().longValue());
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
        dto.setLicensePlate(detail.getLicensePlate());
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
        detail.setLicensePlate(dto.getLicensePlate());
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