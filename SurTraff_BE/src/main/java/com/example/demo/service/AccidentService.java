package com.example.demo.service;

import com.example.demo.model.Accident;
import com.example.demo.repository.AccidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccidentService {
    private final AccidentRepository accidentRepository;

    public List<Accident> getAccident() {
        return accidentRepository.findAll();
    }

    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }


} 