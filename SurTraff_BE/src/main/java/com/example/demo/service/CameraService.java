package com.example.demo.service;

import com.example.demo.DTO.*;
import com.example.demo.model.Camera;
import com.example.demo.model.LaneMovement;
import com.example.demo.model.Zone;
import com.example.demo.model.ZoneLightLane;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.LaneMovementRepository;
import com.example.demo.repository.ZoneLightLaneRepository;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CameraService {

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private ZoneLightLaneRepository zoneLightLaneRepository;

    @Autowired
    private LaneMovementRepository laneMovementRepository;

    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    public Optional<Camera> getCameraById(Long id) {
        return cameraRepository.findById(id);
    }

    @Transactional
    public Camera createCamera(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Transactional
    public void setupCamera(CameraSetupDTO dto) {
        // 1. Tạo camera mới
        Camera camera = new Camera();
        camera.setName(dto.getCameraName());
        camera.setStreamUrl(dto.getCameraUrl());
        camera.setLatitude(dto.getLatitude());
        camera.setLongitude(dto.getLongitude());

        camera = cameraRepository.save(camera);

        // 2. Lưu zone và ánh xạ ID tạm từ FE sang ID thật từ DB
        Map<Long, Zone.ZoneType> zoneIdToType = new HashMap<>();
        Map<Long, Zone> zoneIdToEntity = new HashMap<>();
        Map<Long, Long> tempZoneIdToRealId = new HashMap<>();

        for (ZoneDTO zoneDTO : dto.getZones()) {
            Long tempId = zoneDTO.getId();
            zoneDTO.setId(null);
            zoneDTO.setCameraId(camera.getId());

            Zone zone = zoneDTO.toEntity();
            zone.setZoneType(Zone.ZoneType.valueOf(zoneDTO.getZoneType()));
            zone.setCamera(camera);

            zone = zoneRepository.save(zone);

            zoneIdToType.put(zone.getId(), zone.getZoneType());
            zoneIdToEntity.put(zone.getId(), zone);
            tempZoneIdToRealId.put(tempId, zone.getId());
        }

        // 3. Lưu các ánh xạ Light ↔ Lane
        for (ZoneLightLaneDTO mappingDTO : dto.getZoneLightLaneLinks()) {
            Long lightId = tempZoneIdToRealId.get(mappingDTO.getLightZoneId());
            Long laneId = tempZoneIdToRealId.get(mappingDTO.getLaneZoneId());

            if (!zoneIdToType.containsKey(lightId) || zoneIdToType.get(lightId) != Zone.ZoneType.light) {
                throw new IllegalArgumentException("Invalid light zone ID: " + lightId);
            }
            if (!zoneIdToType.containsKey(laneId) || zoneIdToType.get(laneId) != Zone.ZoneType.lane) {
                throw new IllegalArgumentException("Invalid lane zone ID: " + laneId);
            }

            ZoneLightLane zll = new ZoneLightLane();
            zll.setLightZone(zoneIdToEntity.get(lightId));
            zll.setLaneZone(zoneIdToEntity.get(laneId));
            zoneLightLaneRepository.save(zll);
        }

        // 4. Lưu các hướng di chuyển giữa các làn
        for (LaneMovementDTO movementDTO : dto.getLaneMovements()) {
            Long fromId = tempZoneIdToRealId.get(movementDTO.getFromLaneZoneId());
            Long toId = tempZoneIdToRealId.get(movementDTO.getToLaneZoneId());

            if (!zoneIdToType.containsKey(fromId) || zoneIdToType.get(fromId) != Zone.ZoneType.lane) {
                throw new IllegalArgumentException("Invalid from-lane ID: " + fromId);
            }
            if (!zoneIdToType.containsKey(toId) || zoneIdToType.get(toId) != Zone.ZoneType.lane) {
                throw new IllegalArgumentException("Invalid to-lane ID: " + toId);
            }

            LaneMovement movement = new LaneMovement();
            movement.setFromLaneZone(zoneIdToEntity.get(fromId));
            movement.setToLaneZone(zoneIdToEntity.get(toId));
            laneMovementRepository.save(movement);
        }
    }

    public CameraWithZonesDTO getCameraWithZones(Long id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Camera not found with ID: " + id));

        // 1. Lấy danh sách Zone của camera
        List<ZoneDTO> zoneDTOs = zoneRepository.findByCameraId(camera.getId()).stream()
                .map(zone -> ZoneDTO.builder()
                        .id(zone.getId())
                        .name(zone.getName())
                        .cameraId(camera.getId())
                        .zoneType(zone.getZoneType().name()) // giữ nguyên lowercase
                        .coordinates(zone.getCoordinates())
                        .build())
                .toList();

        // 2. Lấy danh sách ánh xạ Light ↔ Lane
        List<ZoneLightLaneDTO> lightLaneDTOs = zoneLightLaneRepository.findByLightZoneCameraId(camera.getId()).stream()
                .map(link -> ZoneLightLaneDTO.builder()
                        .lightZoneId(link.getLightZone().getId())
                        .laneZoneId(link.getLaneZone().getId())
                        .build())
                .toList();

        // 3. Lấy danh sách hướng di chuyển giữa các làn
        List<LaneMovementDTO> movementDTOs = laneMovementRepository.findByFromLaneZoneCameraId(camera.getId()).stream()
                .map(move -> LaneMovementDTO.builder()
                        .fromLaneZoneId(move.getFromLaneZone().getId())
                        .toLaneZoneId(move.getToLaneZone().getId())
                        .build())
                .toList();

        // 4. Trả về DTO tổng hợp
        return CameraWithZonesDTO.builder()
                .id(camera.getId())
                .name(camera.getName())
                .streamUrl(camera.getStreamUrl())
                .location(camera.getLocation())
                .latitude(camera.getLatitude())
                .longitude(camera.getLongitude())
                .zones(zoneDTOs)
                .zoneLightLaneLinks(lightLaneDTOs)
                .laneMovements(movementDTOs)
                .build();
    }



    @Transactional
    public Camera updateCamera(Long id, Camera cameraDetails) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camera not found"));

        camera.setName(cameraDetails.getName());
        camera.setLocation(cameraDetails.getLocation());


       camera.setLocationAddress(cameraDetails.getLocationAddress());

        camera.setStreamUrl(cameraDetails.getStreamUrl());
        camera.setThumbnail(cameraDetails.getThumbnail());

        return cameraRepository.save(camera);
    }

    @Transactional
    public void deleteCamera(Long id) {
        if (!cameraRepository.existsById(id)) {
            throw new RuntimeException("Camera not found");
        }
        cameraRepository.deleteById(id);
    }
}
