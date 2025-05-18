package com.example.demo.service;

import com.example.demo.repository.AccidentRepository;
import org.springframework.stereotype.Service;

@Service
public class AccidentService {
    private final AccidentRepository accidentRepository;

    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }


} 