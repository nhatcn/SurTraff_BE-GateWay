package com.example.demo.controller;

import com.example.demo.DTO.CameraSetupDTO;
import com.example.demo.DTO.CameraWithZonesDTO;
import com.example.demo.model.Camera;
import com.example.demo.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @GetMapping
    public ResponseEntity<List<Camera>> getAllCameras() {
        return ResponseEntity.ok(cameraService.getAllCameras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CameraWithZonesDTO> getCameraWithZones(@PathVariable Long id) {
        try {
            CameraWithZonesDTO dto = cameraService.getCameraWithZones(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Camera> createCamera(@RequestBody Camera camera) {
        return ResponseEntity.ok(cameraService.createCamera(camera));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Camera> updateCamera(@PathVariable Long id, @RequestBody Camera cameraDetails) {
        try {
            Camera updatedCamera = cameraService.updateCamera(id, cameraDetails);
            return ResponseEntity.ok(updatedCamera);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable Long id) {
        try {
            cameraService.deleteCamera(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/setup")
    public ResponseEntity<String> setupCamera(@RequestBody CameraSetupDTO setupDTO) {
        try {
            cameraService.setupCamera(setupDTO);
            return ResponseEntity.ok("Camera setup successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Setup failed: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Server error: " + ex.getMessage());
        }
    }
}
