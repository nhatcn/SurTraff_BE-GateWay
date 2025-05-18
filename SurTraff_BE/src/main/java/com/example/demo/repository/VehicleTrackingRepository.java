package com.example.demo.repository;

import com.example.demo.model.VehicleTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleTrackingRepository extends JpaRepository<VehicleTracking, Long> {

    List<VehicleTracking> findByCameraId(Long cameraId);
} 