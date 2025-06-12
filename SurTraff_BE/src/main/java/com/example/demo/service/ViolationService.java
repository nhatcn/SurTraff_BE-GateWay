package com.example.demo.service;

import com.example.demo.DTO.ViolationsDTO;
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

    public List<ViolationsDTO> getAllViolations() {
        return violationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ViolationsDTO convertToDTO(Violation violation) {
        ViolationsDTO dto = new ViolationsDTO();
        if (violation.getVehicle() != null) {
            dto.setVehicleId(violation.getVehicle().getId());
        } else {
            dto.setVehicleId(null); // hoặc gán 0, hoặc chuỗi "UNKNOWN", tuỳ bạn muốn xử lý thế nào
        }
        dto.setCreatedAt(violation.getCreatedAt().toString());
        return dto;
    }
}