package com.example.demo.controller;

import com.example.demo.model.Accident;
import com.example.demo.service.AccidentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accident")
@AllArgsConstructor
public class AccidentController {
    private final AccidentService accidentService;

    @GetMapping("/list")
    public ResponseEntity<List<Accident>> getAllAccident() {
        List<Accident> accident = accidentService.getAccident();
        return (accident != null) ? ResponseEntity.status(200).body(accident) : ResponseEntity.notFound().build();
    }


}