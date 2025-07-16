package com.example.demo.service;

import com.example.demo.DTO.AccidentDTO;
import com.example.demo.model.Accident;
import com.example.demo.repository.AccidentRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccidentService {
    private static final Logger logger = LoggerFactory.getLogger(AccidentService.class);

    @Autowired
    private JavaMailSender mailSender; // Inject JavaMailSender

    @Autowired
    private AccidentRepository accidentRepository;

    public List<AccidentDTO> getAllAccidents() {
        List<Accident> accidents = accidentRepository.findAll();
        return accidents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccidentDTO getAccidentById(Long id) {
        Accident accident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
        return convertToDTO(accident);
    }

    public void deleteAccident(Long id) {
        accidentRepository.deleteById(id);
    }

    public Accident updateAccident(Long id, Accident updatedAccident) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tai nạn không tồn tại với ID: " + id));

        if (updatedAccident.getDescription() != null) {
            existingAccident.setDescription(updatedAccident.getDescription());
        }

        return accidentRepository.save(existingAccident);
    }

    public Accident acceptAccident(Long id) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tai nạn không tồn tại với ID: " + id));

        existingAccident.setStatus("Approve");
        Accident updatedAccident = accidentRepository.save(existingAccident);

        // Gửi email thông báo
        try {
            sendApprovalEmail(updatedAccident);
            logger.info("Gửi email thành công tới: {}", updatedAccident.getVehicle().getUser().getEmail());
        } catch (MessagingException e) {
            logger.error("Lỗi khi gửi email cho tai nạn ID: {}", id, e);
        }

        return updatedAccident;
    }

    private void sendApprovalEmail(Accident accident) throws MessagingException {
        String userEmail = accident.getVehicle().getUser().getEmail();
        String fullName = accident.getVehicle().getUser().getFullName();
        String licensePlate = accident.getVehicle().getLicensePlate();
        String location = accident.getLocation();

        String subject = "Thông báo: Tai nạn của bạn đã được ghi nhận";
        String content = String.format(
                "Kính gửi %s,\n\n" +
                        "Chúng tôi đã ghi nhận một tai nạn của bạn với thông tin sau:\n" +
                        "- Mã tai nạn: %d\n" +
                        "- Biển số xe: %s\n" +
                        "- Vị trí: %s\n" +
                        "Trạng thái: Đã phê duyệt.\n" +
                        "Vui lòng liên hệ nếu cần thêm thông tin.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý tai nạn",
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

        dto.setCamera_id(accident.getCamera() != null && accident.getCamera().getId() != null
                ? accident.getCamera().getId().longValue() : null);

        dto.setVehicle_id(accident.getVehicle() != null && accident.getVehicle().getId() != null
                ? accident.getVehicle().getId().longValue() : null);

        dto.setUser_id(accident.getVehicle() != null
                && accident.getVehicle().getUser() != null
                && accident.getVehicle().getUser().getId() != null
                ? accident.getVehicle().getUser().getId().longValue() : null);

        dto.setUser_fullName(
                accident.getVehicle() != null &&
                        accident.getVehicle().getUser() != null
                        ? accident.getVehicle().getUser().getFullName()
                        : null
        );

        dto.setUser_email(
                accident.getVehicle() != null &&
                        accident.getVehicle().getUser() != null
                        ? accident.getVehicle().getUser().getEmail()
                        : null
        );

        dto.setLicensePlate(
                accident.getVehicle() != null
                        ? accident.getVehicle().getLicensePlate()
                        : null
        );

        dto.setName(accident.getVehicle() != null ? accident.getVehicle().getName() : null);

        dto.setDescription(accident.getDescription());

        dto.setImage_url(accident.getImage_url());

        dto.setVideo_url(accident.getVideo_url());

        dto.setLocation(accident.getLocation());

        dto.setStatus(accident.getStatus());

        dto.setLatitude(accident.getCamera() != null ? accident.getCamera().getLatitude() : null);
        dto.setLongitude(accident.getCamera() != null ? accident.getCamera().getLongitude() : null);

        dto.setAccident_time(accident.getAccident_time() != null
                ? java.util.Date.from(accident.getAccident_time().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        dto.setCreated_at(accident.getCreated_at() != null
                ? java.util.Date.from(accident.getCreated_at().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        return dto;
    }







    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }

} 