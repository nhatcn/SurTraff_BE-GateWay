package com.example.demo.service;
import com.example.demo.DTO.AccidentDTO;
import com.example.demo.DTO.ViolationDTO;
import com.example.demo.model.Accident;
import com.example.demo.model.Violation;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViolationService {
    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
    }

    public List<ViolationDTO> getLicensePlate() {
        List<Violation> violations = violationRepository.findAll();
        return violations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ViolationDTO convertToDTO(Violation violation) {
        if (violation == null) return null;

        ViolationDTO dto = new ViolationDTO();

        dto.setId(violation.getId() != null ? violation.getId().longValue() : null);

        dto.setCamera_id(violation.getCamera() != null && violation.getCamera().getId() != null
                ? violation.getCamera().getId().longValue() : null);

        dto.setVehicle_id(violation.getVehicle() != null && violation.getVehicle().getId() != null
                ? violation.getVehicle().getId().longValue() : null);

        dto.setUser_id(violation.getVehicle() != null
                && violation.getVehicle().getUser() != null
                && violation.getVehicle().getUser().getId() != null
                ? violation.getVehicle().getUser().getId().longValue() : null);

        dto.setLicensePlate(violation.getVehicle() != null
                ? violation.getVehicle().getLicensePlate()
                : null);

        dto.setViolation_type_id(violation.getViolationType() != null
                ? violation.getViolationType().getId().longValue()
                : null);

        dto.setType_name(violation.getViolationType() != null
                ? violation.getViolationType().getTypeName()
                : null);

        dto.setCreated_at(violation.getCreatedAt());

        // Lấy location từ camera và gán vào dto
        dto.setLocation(violation.getCamera() != null
                ? violation.getCamera().getLocation()
                : null);

        return dto;
    }


}