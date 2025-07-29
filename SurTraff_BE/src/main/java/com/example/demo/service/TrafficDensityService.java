package com.example.demo.service;


import com.example.demo.DTO.TrafficDensityDTO;
import com.example.demo.model.Camera;
import com.example.demo.model.TrafficDensity;
import com.example.demo.repository.TrafficDensityRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrafficDensityService {
    private final TrafficDensityRepository trafficDensityRepository;

    public TrafficDensityService(TrafficDensityRepository trafficDensityRepository) {
        this.trafficDensityRepository = trafficDensityRepository;
    }

    public List<TrafficDensityDTO> getAllTrafficDensityDTO() {
        List<TrafficDensity> densityList = trafficDensityRepository.findAll();

        return densityList.stream().map(td -> new TrafficDensityDTO(
                td.getId() != null ? td.getId().longValue() : null,
                td.getCamera() != null ? td.getCamera().getId() : null,
                td.getCamera() != null ? td.getCamera().getLocation() : null,
                td.getVehicleCount() != null ? td.getVehicleCount().longValue() : null,
                td.getCreatedAt() != null ? Timestamp.valueOf(td.getCreatedAt()) : null,
                td.getCamera() != null ? td.getCamera().getLatitude() : null,
                td.getCamera() != null ? td.getCamera().getLongitude() : null
        )).collect(Collectors.toList());
    }



} 