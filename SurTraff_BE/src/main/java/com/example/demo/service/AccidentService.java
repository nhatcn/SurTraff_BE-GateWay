package com.example.demo.service;

import com.example.demo.DTO.AccidentDTO;
import com.example.demo.model.Accident;
import com.example.demo.model.Notifications;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.AccidentRepository;
import com.example.demo.repository.NotificationsRepository;
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

    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
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

    public void deleteAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        accident.setIsDelete(true);
        accidentRepository.save(accident);
    }

    public Accident updateAccident(Long id, Accident updatedAccident) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        if (updatedAccident.getDescription() != null) {
            existingAccident.setDescription(updatedAccident.getDescription());
        }

        return accidentRepository.save(existingAccident);
    }

    public Accident acceptAccident(Long id) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        existingAccident.setStatus("Approved");
        Accident updatedAccident = accidentRepository.save(existingAccident);

        try {
            sendApprovalEmail(updatedAccident);
            logger.info("Email successfully sent to: {}", updatedAccident.getVehicle().getUser().getEmail());
        } catch (MessagingException e) {
            logger.error("Error sending email for accident ID: {}", id, e);
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
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = in.readAllBytes();
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
            helper.addInline(imageCid, imageResource, "image/jpeg");
        } catch (Exception e) {
            logger.warn("Failed to load image from URL: {}", imageUrl, e);
        }
        mailSender.send(message);
    }

    public AccidentDTO convertToDTO(Accident accident) {
        if (accident == null) return null;

        AccidentDTO dto = new AccidentDTO();

        dto.setId(accident.getId() != null ? accident.getId().longValue() : null);

        dto.setCameraId(accident.getCamera() != null && accident.getCamera().getId() != null
                ? accident.getCamera().getId().longValue() : null);

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

        dto.setLatitude(accident.getCamera() != null ? accident.getCamera().getLatitude() : null);
        dto.setLongitude(accident.getCamera() != null ? accident.getCamera().getLongitude() : null);

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

    public Accident requestAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        accident.setStatus("Requested");
        return accidentRepository.save(accident);
    }

    public Accident processAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        accident.setStatus("Processed");
        return accidentRepository.save(accident);
    }

    public Accident rejectAccident(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Accident not found with ID: " + id));

        accident.setStatus("Rejected");
        return accidentRepository.save(accident);
    }
}
