package com.example.demo.controller;

import com.example.demo.DTO.AccidentDTO;
import com.example.demo.model.Accident;
import com.example.demo.service.AccidentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/accident")
@AllArgsConstructor
public class AccidentController {
    private final AccidentService accidentService;

    @GetMapping
    public List<AccidentDTO> getAllAccidents() {
        return accidentService.getAllAccidents();
    }

    @GetMapping("/{id}")
    public AccidentDTO getAccidentById(@PathVariable Long id) {
        return accidentService.getAccidentById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccidentDTO> updateAccident(@PathVariable Long id, @RequestBody Accident accident) {
        Accident updatedAccident = accidentService.updateAccident(id, accident);
        AccidentDTO dto = accidentService.convertToDTO(updatedAccident);
        return ResponseEntity.ok(dto); // Trả về AccidentDTO với mã trạng thái 200 OK
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<AccidentDTO> approveAccident(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(AccidentController.class);
        logger.info("Received approve request for accident ID: {}", id);
        try {
            Accident updatedAccident = accidentService.acceptAccident(id);
            AccidentDTO dto = accidentService.convertToDTO(updatedAccident);
            logger.info("Returning AccidentDTO: {}", dto);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error approving accident ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccident(@PathVariable Long id) {
        accidentService.deleteAccident(id);
        return ResponseEntity.noContent().build();
    }
}