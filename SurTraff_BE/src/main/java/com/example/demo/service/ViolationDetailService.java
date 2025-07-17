package com.example.demo.service;

import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.model.ViolationDetail;
import com.example.demo.repository.ViolationDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViolationDetailService {
    @Autowired
    private ViolationDetailRepository repository;

    public List<ViolationDetailDTO> getAllViolations() {
        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ViolationDetailDTO getViolationById(Integer id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private ViolationDetailDTO convertToDTO(ViolationDetail entity) {
        ViolationDetailDTO dto = new ViolationDetailDTO();
        dto.setId(entity.getId());
        dto.setViolationId(entity.getViolation() != null ? entity.getViolation().getId().longValue() : null);
        dto.setViolationTypeId(entity.getViolationType() != null ? entity.getViolationType().getId().longValue() : null);
        dto.setImageUrl(entity.getImageUrl());
        dto.setVideoUrl(entity.getVideoUrl());
        dto.setLocation(entity.getLocation());
        dto.setViolationTime(entity.getViolationTime() != null ? entity.getViolationTime() : null);
        dto.setSpeed(entity.getSpeed());
        dto.setAdditionalNotes(entity.getAdditionalNotes());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : null);
        return dto;
    }
}