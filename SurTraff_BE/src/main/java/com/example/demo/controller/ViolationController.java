package com.example.demo.controller;

import com.example.demo.DTO.ViolationsDTO;
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

    // ✅ API GET /api/violations/all
    @GetMapping("/all")
    public List<ViolationsDTO> getAllViolations() {
        return violationService.getAllViolations();
    }
}