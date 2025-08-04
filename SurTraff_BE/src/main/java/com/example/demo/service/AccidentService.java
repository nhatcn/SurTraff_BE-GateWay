package com.example.demo.service;

import com.example.demo.DTO.AccidentDTO;
import com.example.demo.model.Accident;
import com.example.demo.model.Camera;
import com.example.demo.model.Notifications;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.AccidentRepository;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.VehicleRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccidentService {
    private static final Logger logger = LoggerFactory.getLogger(AccidentService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccidentRepository accidentRepository;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    // Constructor injection is preferred for required dependencies
    public AccidentService(AccidentRepository accidentRepository, NotificationsRepository notificationsRepository,
                           CloudinaryService cloudinaryService, CameraRepository cameraRepository,
                           VehicleRepository vehicleRepository, JavaMailSender mailSender) {
        this.accidentRepository = accidentRepository;
        this.notificationsRepository = notificationsRepository;
        this.cloudinaryService = cloudinaryService;
        this.cameraRepository = cameraRepository;
        this.vehicleRepository = vehicleRepository;
        this.mailSender = mailSender;
    }

    public List<AccidentDTO> getAllAccidents() {
        List<Accident> accidents = accidentRepository.findByIsDeleteFalse();
        return accidents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccidentDTO getAccidentById(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        return convertToDTO(accident);
    }

    @Transactional // Add Transactional for delete operations
    public void deleteAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        accident.setIsDelete(true);
        accidentRepository.save(accident);
        logger.info("Accident with ID {} marked as deleted.", id);
    }

    @Transactional // Add Transactional for update operations
    public Accident updateAccident(Long id, Accident updatedAccident) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        if (updatedAccident.getDescription() != null) {
            existingAccident.setDescription(updatedAccident.getDescription());
        }
        Accident savedAccident = accidentRepository.save(existingAccident);
        logger.info("Accident with ID {} updated.", savedAccident.getId());
        return savedAccident;
    }

    @Transactional // Add Transactional for accept operations
    public Accident acceptAccident(Long id) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        existingAccident.setStatus("Approved");
        Accident updatedAccident = accidentRepository.save(existingAccident);
        logger.info("Accident with ID {} approved.", updatedAccident.getId());
        try {
            sendApprovalEmail(updatedAccident);
            logger.info("Email successfully sent to: {}", updatedAccident.getVehicle().getUser().getEmail());
        } catch (MessagingException e) {
            logger.error("Error sending email for accident ID: {}", id, e);
        } catch (Exception e) { // Catch any other potential exceptions during email sending
            logger.error("Unexpected error during email sending for accident ID: {}", id, e);
        }
        try {
            User user = updatedAccident.getVehicle().getUser();
            Vehicle vehicle = updatedAccident.getVehicle();
            String message = String.format(
                    "Your vehicle %s was %s in %s.",
                    vehicle.getLicensePlate(),
                    updatedAccident.getDescription(),
                    updatedAccident.getLocation()
            );
            Notifications notification = Notifications.builder()
                    .user(user)
                    .vehicle(vehicle)
                    .accident(updatedAccident)
                    .violation(null)
                    .message(message)
                    .notification_type("accident")
                    .read(false)
                    .created_at(LocalDateTime.now())
                    .build();
            notificationsRepository.save(notification);
            logger.info("Notification saved for user: {}", user.getId());
        } catch (Exception e) {
            logger.error("Failed to save notification for accident ID: {}", id, e);
        }
        return updatedAccident;
    }

    private void sendApprovalEmail(Accident accident) throws MessagingException {
        String userEmail = accident.getVehicle().getUser().getEmail();
        String fullName = accident.getVehicle().getUser().getFullName();
        String licensePlate = accident.getVehicle().getLicensePlate();
        String location = accident.getLocation();
        String subject = "Notification: Your accident has been recorded";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userEmail);
        helper.setSubject(subject);

        String imageCid = "accidentImage001";
        String htmlContent = String.format(
                "<p>Dear %s,</p>" +
                        "<p>We have recorded an accident related to your vehicle:</p>" +
                        "<ul>" +
                        "<li><strong>Accident ID:</strong> %d</li>" +
                        "<li><strong>License Plate:</strong> %s</li>" +
                        "<li><strong>Location:</strong> %s</li>" +
                        "<li><strong>Status:</strong> Approved</li>" +
                        "</ul>" +
                        "<p><strong>Accident Image:</strong></p>" +
                        "<img src='cid:%s' width='500'/>" +
                        "<p>If you have questions, contact support.</p>" +
                        "<p>Regards,<br>Accident Management System</p>",
                fullName, accident.getId(), licensePlate, location, imageCid
        );
        helper.setText(htmlContent, true);

        String imageUrl = accident.getImage_url();
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

    public AccidentDTO convertToDTO(Accident accident) {
        if (accident == null) return null;
        AccidentDTO dto = new AccidentDTO();
        dto.setId(accident.getId() != null ? accident.getId().longValue() : null);
        dto.setCameraId(accident.getCamera() != null && accident.getCamera().getId() != null
                ? accident.getCamera().getId().longValue() : null);
        dto.setLatitude(accident.getCamera() != null ? accident.getCamera().getLatitude() : null);
        dto.setLongitude(accident.getCamera() != null ? accident.getCamera().getLongitude() : null);
        dto.setCameraName(accident.getCamera() != null ? accident.getCamera().getName() : null);
        dto.setCameraLocation(accident.getCamera() != null ? accident.getCamera().getLocation() : null);
        dto.setVehicleId(accident.getVehicle() != null && accident.getVehicle().getId() != null
                ? accident.getVehicle().getId().longValue() : null);
        dto.setUserId(accident.getVehicle() != null &&
                accident.getVehicle().getUser() != null &&
                accident.getVehicle().getUser().getId() != null
                ? accident.getVehicle().getUser().getId().longValue() : null);
        dto.setUserFullName(accident.getVehicle() != null &&
                accident.getVehicle().getUser() != null
                ? accident.getVehicle().getUser().getFullName() : null);
        dto.setUserEmail(accident.getVehicle() != null &&
                accident.getVehicle().getUser() != null
                ? accident.getVehicle().getUser().getEmail() : null);
        dto.setLicensePlate(accident.getVehicle() != null
                ? accident.getVehicle().getLicensePlate() : null);
        dto.setName(accident.getVehicle() != null
                ? accident.getVehicle().getName() : null);
        dto.setDescription(accident.getDescription());
        dto.setImageUrl(accident.getImage_url());
        dto.setVideoUrl(accident.getVideo_url());
        dto.setLocation(accident.getLocation());
        dto.setStatus(accident.getStatus());
        dto.setAccidentTime(accident.getAccident_time() != null
                ? java.util.Date.from(accident.getAccident_time().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);
        dto.setCreatedAt(accident.getCreated_at() != null
                ? java.util.Date.from(accident.getCreated_at().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);
        dto.setIsDelete(accident.getIsDelete());
        return dto;
    }

    public List<AccidentDTO> getAccidentsByUserId(Long userId) {
        List<Accident> accidents = accidentRepository.findByVehicle_User_IdAndIsDeleteFalse(userId);
        return accidents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional // Add Transactional for request operations
    public Accident requestAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        accident.setStatus("Requested");
        Accident savedAccident = accidentRepository.save(accident);
        logger.info("Accident with ID {} requested.", savedAccident.getId());
        return savedAccident;
    }

    @Transactional // Add Transactional for process operations
    public Accident processAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        accident.setStatus("Processed");
        Accident savedAccident = accidentRepository.save(accident);
        logger.info("Accident with ID {} processed.", savedAccident.getId());
        return savedAccident;
    }

    @Transactional // Add Transactional for reject operations
    public Accident rejectAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));
        accident.setStatus("Rejected");
        Accident savedAccident = accidentRepository.save(accident);
        logger.info("Accident with ID {} rejected.", savedAccident.getId());
        return savedAccident;
    }

    @Transactional // Crucial for database write operations
    public AccidentDTO addAccident(AccidentDTO accidentDTO, MultipartFile imageFile, MultipartFile videoFile) throws IOException {
        String imageUrl = null;
        String videoUrl = null;
        logger.info("Attempting to add new accident. Camera ID: {}, Vehicle ID: {}", accidentDTO.getCameraId(), accidentDTO.getVehicleId());

        // 1. Upload image and video to Cloudinary
        // Nếu ảnh là bắt buộc, ném ngoại lệ nếu upload thất bại
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadImage(imageFile);
                logger.info("Image uploaded to Cloudinary. URL: {}", imageUrl);
            } catch (Exception e) {
                logger.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
                throw new IOException("Failed to upload image for accident", e); // Ném lại ngoại lệ
            }
        } else {
            logger.warn("No image file provided for accident. This might be an issue if image is required.");
            // Nếu ảnh là bắt buộc và không được cung cấp, bạn có thể ném ngoại lệ ở đây
            // throw new IllegalArgumentException("Image file is required for an accident.");
        }

        // Nếu video là bắt buộc, ném ngoại lệ nếu upload thất bại
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                videoUrl = cloudinaryService.uploadVideo(videoFile);
                logger.info("Video uploaded to Cloudinary. URL: {}", videoUrl);
            } catch (Exception e) {
                logger.error("Error uploading video to Cloudinary: {}", e.getMessage(), e);
                throw new IOException("Failed to upload video for accident", e); // Ném lại ngoại lệ
            }
        } else {
            logger.warn("No video file provided for accident. This might be an issue if video is required.");
            // Nếu video là bắt buộc và không được cung cấp, bạn có thể ném ngoại lệ ở đây
            // throw new IllegalArgumentException("Video file is required for an accident.");
        }

        // 2. Fetch Camera and Vehicle entities
        Camera camera = cameraRepository.findById(accidentDTO.getCameraId())
                .orElseThrow(() -> {
                    logger.error("Camera not found with ID: {}", accidentDTO.getCameraId());
                    return new EntityNotFoundException("Camera not found with ID: " + accidentDTO.getCameraId());
                });
        logger.info("Found Camera: {}", camera.getName());

        Vehicle vehicle = vehicleRepository.findById(accidentDTO.getVehicleId())
                .orElseThrow(() -> {
                    logger.error("Vehicle not found with ID: {}", accidentDTO.getVehicleId());
                    return new EntityNotFoundException("Vehicle not found with ID: " + accidentDTO.getVehicleId());
                });
        logger.info("Found Vehicle: {}", vehicle.getLicensePlate());

        // 3. Create and populate Accident entity
        Accident accident = Accident.builder()
                .camera(camera)
                .vehicle(vehicle)
                .image_url(imageUrl)
                .video_url(videoUrl)
                .description(accidentDTO.getDescription())
                .location(accidentDTO.getLocation())
                .accident_time(accidentDTO.getAccidentTime() != null ?
                        accidentDTO.getAccidentTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .status("pending") // Set initial status
                .isDelete(false)
                .created_at(LocalDateTime.now())
                .build();
        logger.info("Attempting to save accident entity: {}", accident);

        // 4. Save the accident to the database
        Accident savedAccident = accidentRepository.save(accident);
        logger.info("Accident saved successfully with ID: {}", savedAccident.getId());

        // 5. Convert and return DTO
        return convertToDTO(savedAccident);
    }
}