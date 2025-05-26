package com.example.demo.controller;

import com.example.demo.DTO.ZoneLightLaneDTO;
import com.example.demo.service.ZoneLightLaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone-light-lanes")
public class ZoneLightLaneController {

    @Autowired
    private ZoneLightLaneService zoneLightLaneService;

    @GetMapping
    public ResponseEntity<List<ZoneLightLaneDTO>> getAllZoneLightLanes() {
        return ResponseEntity.ok(zoneLightLaneService.getAllZoneLightLanes());
    }

    @GetMapping("/lane/{laneZoneId}")
    public ResponseEntity<List<ZoneLightLaneDTO>> getByLaneZoneId(@PathVariable Long laneZoneId) {
        return ResponseEntity.ok(zoneLightLaneService.getByLaneZoneId(laneZoneId));
    }

    @PostMapping
    public ResponseEntity<ZoneLightLaneDTO> createZoneLightLane(@RequestBody ZoneLightLaneDTO zoneLightLaneDTO) {
        try {
            ZoneLightLaneDTO created = zoneLightLaneService.create(zoneLightLaneDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZoneLightLane(@PathVariable Long id) {
        try {
            zoneLightLaneService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 