package com.example.demo.service;

import com.example.demo.model.ViolationType;
import com.example.demo.repository.ViolationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationTypesService {

    private final ViolationTypeRepository violationTypeRepository;

    public ViolationTypesService(ViolationTypeRepository violationTypeRepository) {
        this.violationTypeRepository = violationTypeRepository;
    }

    public List<ViolationType> getAll(){
        return violationTypeRepository.findAll();
    }
} 