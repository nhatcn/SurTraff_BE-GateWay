package com.example.demo.service;


import com.example.demo.repository.TrafficDensityRepository;
import org.springframework.stereotype.Service;

@Service
public class TrafficDensityService {
    private final TrafficDensityRepository trafficDensityRepository;

    public TrafficDensityService(TrafficDensityRepository trafficDensityRepository) {
        this.trafficDensityRepository = trafficDensityRepository;
    }


} 