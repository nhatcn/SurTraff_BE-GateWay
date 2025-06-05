package com.example.demo.controller;

import com.example.demo.model.Accident;
import com.example.demo.service.AccidentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accident")
@AllArgsConstructor
public class AccidentController {
    private final AccidentService accidentService;

    @GetMapping()
    public ResponseEntity<List<Accident>> getAllAccident() {
        List<Accident> accident = accidentService.getAccident();
        return (accident != null) ? ResponseEntity.status(200).body(accident) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public Accident getAccidentById(@PathVariable Long id) {
        return accidentService.getAccidentById(id);
    }

    @PutMapping("/{id}")
    public Accident updateAccident(@PathVariable Long id, @RequestBody Accident accident) {
        return accidentService.updateAccident(id, accident);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccident(@PathVariable Long id) {
        accidentService.deleteAccident(id);
        return ResponseEntity.noContent().build();
    }

}