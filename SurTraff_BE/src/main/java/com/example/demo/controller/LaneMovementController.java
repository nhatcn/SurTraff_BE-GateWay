package com.example.demo.controller;

import com.example.demo.DTO.LaneMovementDTO;
import com.example.demo.service.LaneMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lane-movements")
public class LaneMovementController {

    @Autowired
    private LaneMovementService laneMovementService;

    @GetMapping
    public ResponseEntity<List<LaneMovementDTO>> getAllLaneMovements() {
        return ResponseEntity.ok(laneMovementService.getAllLaneMovements());
    }

    @GetMapping("/from/{fromLaneZoneId}")
    public ResponseEntity<List<LaneMovementDTO>> getLaneMovementsByFromLaneZoneId(@PathVariable Long fromLaneZoneId) {
        return ResponseEntity.ok(laneMovementService.getLaneMovementsByFromLaneZoneId(fromLaneZoneId));
    }

    @GetMapping("/to/{toLaneZoneId}")
    public ResponseEntity<List<LaneMovementDTO>> getLaneMovementsByToLaneZoneId(@PathVariable Long toLaneZoneId) {
        return ResponseEntity.ok(laneMovementService.getLaneMovementsByToLaneZoneId(toLaneZoneId));
    }

    @PostMapping
    public ResponseEntity<LaneMovementDTO> createLaneMovement(@RequestBody LaneMovementDTO laneMovementDTO) {
        try {
            LaneMovementDTO created = laneMovementService.createLaneMovement(laneMovementDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idFrom}/{idTo}")
    public ResponseEntity<Void> deleteLaneMovement(@PathVariable Long idFrom,@PathVariable Long idTo) {
        try {
            laneMovementService.deleteLaneMovement(idFrom,idTo);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 