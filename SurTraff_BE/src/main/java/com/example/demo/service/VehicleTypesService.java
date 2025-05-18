package com.example.demo.service;

import com.example.demo.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleTypesService {
    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleTypesService(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }


} 