package com.example.demo.service;

import com.example.demo.model.Accident;
import com.example.demo.repository.AccidentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccidentService {
    private final AccidentRepository accidentRepository;

    public List<Accident> getAccident() {
        return accidentRepository.findAll();
    }

    public Accident getAccidentById(Long id) {
        return accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vi phạm không tồn tại với ID: " + id));
    }

    public void deleteAccident(Long id) {
        accidentRepository.deleteById(id);
    }

    public Accident updateAccident(Long id, Accident updatedAccident) {
        Accident existingAccident = accidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tai nạn không tồn tại với ID: " + id));


        if (updatedAccident.getDescription() != null) {
            existingAccident.setDescription(updatedAccident.getDescription());
        }

        if (updatedAccident.getAccident_time() != null) {
            existingAccident.setAccident_time(updatedAccident.getAccident_time());
        }

        if (updatedAccident.getCreated_at() != null) {
            existingAccident.setCreated_at(updatedAccident.getCreated_at());
        }

        return accidentRepository.save(existingAccident);
    }




    public AccidentService(AccidentRepository accidentRepository) {
        this.accidentRepository = accidentRepository;
    }

} 