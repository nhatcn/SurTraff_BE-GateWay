package com.example.demo.controller;

import com.example.demo.DTO.ObstacleDTO;
import com.example.demo.service.ObstacleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obstacles")
@AllArgsConstructor
public class ObstacleController {

    private final ObstacleService obstacleService;

    @GetMapping
    public ResponseEntity<List<ObstacleDTO>> getAllObstacles() {
        List<ObstacleDTO> obstacles = obstacleService.getAllObstacles();
        return ResponseEntity.ok(obstacles);
    }

    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<List<ObstacleDTO>> getObstaclesByCamera(@PathVariable Long cameraId) {
        List<ObstacleDTO> obstacles = obstacleService.getObstaclesByCamera(cameraId);
        return ResponseEntity.ok(obstacles);
    }
}