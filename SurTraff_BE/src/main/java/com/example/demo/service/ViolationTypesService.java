package com.example.demo.service;

import com.example.demo.repository.ViolationTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ViolationTypesService {
    private final ViolationTypeRepository violationTypeRepository;

    public ViolationTypesService(ViolationTypeRepository violationTypeRepository) {
        this.violationTypeRepository = violationTypeRepository;
    }


} 