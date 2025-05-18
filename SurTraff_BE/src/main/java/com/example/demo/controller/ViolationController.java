package com.example.demo.controller;

import com.example.demo.service.ViolationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/violations")
@AllArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

}