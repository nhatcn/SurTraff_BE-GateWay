package com.example.demo.service;

import com.example.demo.DTO.ZoneLightLaneDTO;
import com.example.demo.model.Zone;
import com.example.demo.model.ZoneLightLane;
import com.example.demo.repository.ZoneLightLaneRepository;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneLightLaneService {
    @Autowired
    private ZoneLightLaneRepository zoneLightLaneRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    private ZoneLightLaneDTO convertToDTO(ZoneLightLane entity) {
        return ZoneLightLaneDTO.builder()
                .lightZoneId(entity.getLightZone().getId())
                .laneZoneId(entity.getLaneZone().getId())
                .build();
    }

    public List<ZoneLightLaneDTO> getAllZoneLightLanes() {
        return zoneLightLaneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ZoneLightLaneDTO> getByLaneZoneId(Long laneZoneId) {
        return zoneLightLaneRepository.findByLaneZoneId(laneZoneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ZoneLightLaneDTO create(ZoneLightLaneDTO dto) {
        ZoneLightLane entity = new ZoneLightLane();
        entity.setLightZone(zoneRepository.findById(dto.getLightZoneId())
                .orElseThrow(() -> new RuntimeException("Light zone not found")));
        entity.setLaneZone(zoneRepository.findById(dto.getLaneZoneId())
                .orElseThrow(() -> new RuntimeException("Lane zone not found")));
        return convertToDTO(zoneLightLaneRepository.save(entity));
    }

    public boolean delete(Long lightZoneId) {
        if (zoneLightLaneRepository.existsById(lightZoneId)) {
            zoneLightLaneRepository.deleteById(lightZoneId);
            return true;
        }
        return false;
    }
}
