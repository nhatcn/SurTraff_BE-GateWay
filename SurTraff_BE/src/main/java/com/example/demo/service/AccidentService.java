package com.example.demo.service;

import com.example.demo.DTO.AccidentDTO;
import com.example.demo.model.Accident;
import com.example.demo.repository.AccidentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccidentService {
    private static final Logger logger = LoggerFactory.getLogger(AccidentService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccidentRepository accidentRepository;

    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }

    public List<AccidentDTO> getAllAccidents() {
        List<Accident> accidents = accidentRepository.findAll();
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
        accidentRepository.deleteById(id);
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

        return updatedAccident;
    }

    private void sendApprovalEmail(Accident accident) throws MessagingException {
        String userEmail = accident.getVehicle().getUser().getEmail();
        String fullName = accident.getVehicle().getUser().getFullName();
        String licensePlate = accident.getVehicle().getLicensePlate();
        String location = accident.getLocation();

        String subject = "Notification: Your accident has been recorded";

        String content = String.format(
                "Dear %s,\n\n" +
                        "We have recorded an accident related to your vehicle with the following details:\n\n" +
                        "- Accident ID: %d\n" +
                        "- License Plate: %s\n" +
                        "- Location: %s\n" +
                        "- Status: Approved\n\n" +
                        "If you have any questions or require additional information, please contact our support team.\n\n" +
                        "Best regards,\n" +
                        "Accident Management System",
                fullName, accident.getId(), licensePlate, location
        );

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(content);
        mailSender.send(mimeMessage);
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

        return dto;
    }
}
