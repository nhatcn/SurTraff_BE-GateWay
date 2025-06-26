package com.example.demo.controller;

import com.example.demo.DTO.TrafficDensityDTO;
import com.example.demo.model.TrafficDensity;
import com.example.demo.service.CameraService;
import com.example.demo.service.TrafficDensityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trafficdensity")
@AllArgsConstructor
public class TrafficDensityController {

    @Autowired
    private TrafficDensityService trafficDensityService;

    @GetMapping
    public ResponseEntity<List<TrafficDensityDTO>> getAllTrafficDensity() {
        return ResponseEntity.ok(trafficDensityService.getAllTrafficDensityDTO());
    }
}
