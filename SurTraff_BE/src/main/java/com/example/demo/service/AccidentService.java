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
import java.io.File;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ByteArrayResource;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.ByteArrayOutputStream;

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

        String cid = "accidentImage001";
        String content = String.format(
                "Kính gửi %s,<br><br>" +
                        "Chúng tôi đã ghi nhận một tai nạn của bạn với thông tin sau:<br>" +
                        "- Mã tai nạn: %d<br>" +
                        "- Biển số xe: %s<br>" +
                        "- Vị trí: %s<br>" +
                        "Trạng thái: <b>Đã phê duyệt</b>.<br>" +
                        "Hình ảnh tai nạn:<br>" +
                        "<img src='cid:%s' width='500'/><br>" +
                        "Vui lòng liên hệ nếu cần thêm thông tin.<br><br>" +
                        "Trân trọng,<br>" +
                        "Hệ thống quản lý tai nạn",
                fullName, accident.getId(), licensePlate, location, cid
        );

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // true = HTML content

        String imageUrl = accident.getImage_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Tải ảnh từ URL
                URL url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                try (InputStream inputStream = connection.getInputStream()) {
                    // Đọc InputStream thành byte[]
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    byte[] imageBytes = baos.toByteArray();

                    InputStreamSource imageSource = new ByteArrayResource(imageBytes);
                    // Thêm ảnh inline với content-id
                    helper.addInline(cid, imageSource, connection.getContentType());
                }
            } catch (Exception e) {
                logger.error("Lỗi tải ảnh từ URL để gửi mail: " + imageUrl, e);
            }
        }

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