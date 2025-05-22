package com.example.demo.service;

import com.example.demo.DTO.LaneMovementDTO;
import com.example.demo.model.LaneMovement;
import com.example.demo.model.Zone;
import com.example.demo.repository.LaneMovementRepository;
import com.example.demo.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaneMovementService {
    @Autowired
    private LaneMovementRepository laneMovementRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    private LaneMovementDTO convertToDTO(LaneMovement entity) {
        return LaneMovementDTO.builder()
                .fromLaneZoneId(entity.getFromLaneZone().getId())
                .toLaneZoneId(entity.getToLaneZone().getId())
                .build();
    }

    public List<LaneMovementDTO> getAllLaneMovements() {
        return laneMovementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LaneMovementDTO> getLaneMovementsByFromLaneZoneId(Long fromLaneZoneId) {
        return laneMovementRepository.findByFromLaneZoneId(fromLaneZoneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LaneMovementDTO> getLaneMovementsByToLaneZoneId(Long toLaneZoneId) {
        return laneMovementRepository.findByToLaneZoneId(toLaneZoneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LaneMovementDTO createLaneMovement(LaneMovementDTO dto) {
        Zone fromZone = zoneRepository.findById(dto.getFromLaneZoneId())
                .orElseThrow(() -> new RuntimeException("From zone not found"));
        Zone toZone = zoneRepository.findById(dto.getToLaneZoneId())
                .orElseThrow(() -> new RuntimeException("To zone not found"));

        LaneMovement laneMovement = new LaneMovement();
        laneMovement.setFromLaneZone(fromZone);
        laneMovement.setToLaneZone(toZone);

        return convertToDTO(laneMovementRepository.save(laneMovement));
    }

    public boolean deleteLaneMovement(Long fromLaneZoneId, Long toLaneZoneId) {
        laneMovementRepository.deleteByFromLaneZoneIdAndToLaneZoneId(fromLaneZoneId, toLaneZoneId);
        return true;
    }
}
