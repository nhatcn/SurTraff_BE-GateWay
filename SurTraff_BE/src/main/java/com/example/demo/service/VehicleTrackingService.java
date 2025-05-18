package com.example.demo.service;

import com.example.demo.repository.VehicleTrackingRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleTrackingService {
    private final VehicleTrackingRepository vehicleTrackingRepository;

    public VehicleTrackingService(VehicleTrackingRepository vehicleTrackingRepository) {
        this.vehicleTrackingRepository = vehicleTrackingRepository;
    }


} 