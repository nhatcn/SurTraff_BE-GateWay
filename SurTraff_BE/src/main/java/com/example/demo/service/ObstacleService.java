package com.example.demo.service;

import com.example.demo.repository.ObstacleRepository;
import org.springframework.stereotype.Service;

@Service
public class ObstacleService {
    private final ObstacleRepository obstacleRepository;

    public ObstacleService(ObstacleRepository obstacleRepository) {
        this.obstacleRepository = obstacleRepository;
    }


} 