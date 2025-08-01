package com.example.demo.controller;

import com.example.demo.repository.ViolationTypeRepository;
import com.example.demo.service.ViolationTypesService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/violation-type")
public class ViolationTypeController {

    @Autowired

    ViolationTypesService violationTypesService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(violationTypesService.getAll());
    }
}
