package com.example.demo.controller;

import com.example.demo.DTO.ZoneDTO;
import com.example.demo.model.Zone;
import com.example.demo.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping
    public ResponseEntity<List<ZoneDTO>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getZoneById(@PathVariable Long id) {
        return zoneService.getZoneById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/last-zone-id")
    public ResponseEntity<?> getLatestZoneId(){
        return ResponseEntity.ok(zoneService.getLatestZoneId());
    }
    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<List<ZoneDTO>> getZonesByCameraId(@PathVariable Long cameraId) {
        return ResponseEntity.ok(zoneService.getZonesByCameraId(cameraId));
    }

    @GetMapping("/type/{zoneType}")
    public ResponseEntity<List<ZoneDTO>> getZonesByType(@PathVariable Zone.ZoneType zoneType) {
        return ResponseEntity.ok(zoneService.getZonesByType(zoneType));
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> createZone(@RequestBody ZoneDTO zoneDTO) {
        return ResponseEntity.ok(zoneService.createZone(zoneDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable Long id, @RequestBody ZoneDTO zoneDTO) {
        try {
            ZoneDTO updatedZone = zoneService.updateZone(id, zoneDTO);
            return ResponseEntity.ok(updatedZone);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (zoneService.deleteZone(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 