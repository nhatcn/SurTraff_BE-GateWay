package com.example.demo.service;

import com.example.demo.model.Accident;
import com.example.demo.repository.AccidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccidentService {
    private final AccidentRepository accidentRepository;

    // Constructor injection
    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }

    // Lấy tất cả accident
    public List<Accident> getAllAccidents() {
        return accidentRepository.findAll();
    }

    // Lấy accident theo cameraId (nếu cần)
    public List<Accident> getAccidentsByCameraId(Long cameraId) {
        return accidentRepository.findByCameraId(cameraId);
    }
}