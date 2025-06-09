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
        dto.setVehicleId(violation.getVehicle().getId());
        dto.setCreatedAt(violation.getCreatedAt().toString());
        dto.setVehicleId(violation.getVehicle().getId());
        return dto;
    }
}