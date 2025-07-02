package com.example.demo.service;

import com.example.demo.DTO.VehicleStatisticsDTO;
import com.example.demo.model.VehicleStatistics;
import com.example.demo.repository.VehicleStatisticsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VehicleStatisticsService {

    private final VehicleStatisticsRepository vehicleTrackingRepository;

    public List<VehicleStatisticsDTO> getAllVehicleTracking() {
        return vehicleTrackingRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private VehicleStatisticsDTO toDTO(VehicleStatistics entity) {
        VehicleStatisticsDTO dto = new VehicleStatisticsDTO();
        dto.setCameraId(entity.getCamera() != null && entity.getCamera().getId() != null
                ? entity.getCamera().getId().intValue()
                : null);
        dto.setLicensePlate(entity.getLicensePlate());
        dto.setVehicleTypeId(entity.getVehicleType() != null && entity.getVehicleType().getId() != null
                ? entity.getVehicleType().getId().intValue()
                : null);
        dto.setVehicleColor(entity.getVehicleColor());
        dto.setVehicleBrand(entity.getVehicleBrand());
        dto.setSpeed(entity.getSpeed() != null ? entity.getSpeed().doubleValue() : null);
        dto.setLocation(null); // Nếu entity có location thì set, không thì để null
        dto.setImageUrl(entity.getImageUrl());
        dto.setDetectionTime(entity.getDetectionTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}