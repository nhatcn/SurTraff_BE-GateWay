package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.DTO.ViolationDetailDTO;
import com.example.demo.service.ViolationDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/violationdetail")
public class ViolationDetailController {
    @Autowired
    private ViolationDetailService service;

    @GetMapping("/all")
    public List<ViolationDetailDTO> getAllViolations() {
        return service.getAllViolations();
    }

    @GetMapping("/{id}")
    public ViolationDetailDTO getViolationById(@PathVariable Integer id) {
        return service.getViolationById(id);
    }
}