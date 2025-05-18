package com.example.demo.repository;

import com.example.demo.model.TrafficDensity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficDensityRepository extends JpaRepository<TrafficDensity, Long> {

    List<TrafficDensity> findByCameraId(Long cameraId);
} 