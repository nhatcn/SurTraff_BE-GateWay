package com.example.demo.repository;

import com.example.demo.model.VehicleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleStatisticsRepository extends JpaRepository<VehicleStatistics, Long> {

    List<VehicleStatistics> findByCameraId(Long cameraId);
} 