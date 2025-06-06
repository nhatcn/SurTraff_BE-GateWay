package com.example.demo.controller;

import com.example.demo.model.Accident;
import com.example.demo.service.AccidentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidents")
public class AccidentController {
    @Autowired
    private AccidentService accidentService;

    @GetMapping("/all")
    public ResponseEntity<List<Accident>> getAllAccidents() {
        return ResponseEntity.ok(accidentService.getAllAccidents());
    }

    @GetMapping("/by-camera/{cameraId}")
    public ResponseEntity<List<Accident>> getByCamera(@PathVariable Long cameraId) {
        return ResponseEntity.ok(accidentService.getAccidentsByCameraId(cameraId));
    }
}