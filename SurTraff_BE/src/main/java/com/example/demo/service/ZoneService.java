package com.example.demo.service;
import com.example.demo.DTO.ZoneDTO;
import com.example.demo.model.Camera;
import com.example.demo.model.Zone;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ZoneService {
    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private CameraRepository cameraRepository;

    private ZoneDTO convertToDTO(Zone zone) {
        return ZoneDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .cameraId(zone.getCamera() != null ? zone.getCamera().getId() : null)
                .zoneType(zone.getZoneType().name())
                .coordinates(zone.getCoordinates())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ZoneDTO> getZoneById(Long id) {
        return zoneRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ZoneDTO> getZonesByCameraId(Long cameraId) {
        return zoneRepository.findByCameraId(cameraId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ZoneDTO> getZonesByType(Zone.ZoneType zoneType) {
        return zoneRepository.findByZoneType(zoneType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ZoneDTO createZone(ZoneDTO dto) {
        Zone zone = dto.toEntity();
        zone.setCreatedAt(LocalDateTime.now());
        zone.setUpdatedAt(LocalDateTime.now());

        if (dto.getCameraId() != null) {
            Camera camera = cameraRepository.findById(dto.getCameraId())
                    .orElseThrow(() -> new RuntimeException("Camera not found"));
            zone.setCamera(camera);
        }
        return convertToDTO(zoneRepository.save(zone));
    }

    @Transactional
    public ZoneDTO updateZone(Long id, ZoneDTO dto) {
        return zoneRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setZoneType(Zone.ZoneType.valueOf(dto.getZoneType()));
                    existing.setCoordinates(dto.getCoordinates());
                    existing.setUpdatedAt(LocalDateTime.now());

                    if (dto.getCameraId() != null) {
                        Camera camera = cameraRepository.findById(dto.getCameraId())
                                .orElseThrow(() -> new RuntimeException("Camera not found"));
                        existing.setCamera(camera);
                    }
                    return convertToDTO(zoneRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    public boolean deleteZone(Long id) {
        if (zoneRepository.existsById(id)) {
            zoneRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
