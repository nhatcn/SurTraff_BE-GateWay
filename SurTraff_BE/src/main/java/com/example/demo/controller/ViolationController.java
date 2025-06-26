package com.example.demo.controller;

import com.example.demo.DTO.ViolationDTO;
import com.example.demo.service.ViolationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@AllArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

    @GetMapping
    public List<ViolationDTO> getAllViolations() {
        return violationService.getLicensePlate();
    }

}