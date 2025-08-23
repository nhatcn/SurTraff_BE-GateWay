package com.example.demo.service;

import com.example.demo.DTO.ObstacleDTO;
import com.example.demo.model.Obstacle;
import com.example.demo.repository.ObstacleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ObstacleService {

    private final ObstacleRepository obstacleRepository;

    public List<ObstacleDTO> getAllObstacles() {
        return obstacleRepository.findByIsDeleteFalseOrderByDetectionTimeDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ObstacleDTO> getObstaclesByCamera(Long cameraId) {
        return obstacleRepository.findByCameraIdAndIsDeleteFalseOrderByDetectionTimeDesc(cameraId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ObstacleDTO toDTO(Obstacle entity) {
        ObstacleDTO dto = new ObstacleDTO();
        dto.setId(entity.getId());
        dto.setCameraId(entity.getCamera() != null && entity.getCamera().getId() != null
                ? entity.getCamera().getId().intValue()
                : null);
        dto.setCameraName(entity.getCamera() != null && entity.getCamera().getName() != null
                ? entity.getCamera().getName()
                : "Unknown Camera");
        dto.setObstacleType(entity.getObstacleType());
        dto.setImageUrl(entity.getImageUrl());
        dto.setLocation(entity.getLocation());
        dto.setDetectionTime(entity.getDetectionTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLatitude(entity.getCamera() != null && entity.getCamera().getLatitude() != null
                ? entity.getCamera().getLatitude()
                : null);
        dto.setLongitude(entity.getCamera() != null && entity.getCamera().getLongitude() != null
                ? entity.getCamera().getLongitude()
                : null);
        return dto;
    }
}