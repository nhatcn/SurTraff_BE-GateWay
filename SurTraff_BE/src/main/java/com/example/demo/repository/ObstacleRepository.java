package com.example.demo.repository;

import com.example.demo.model.Obstacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ObstacleRepository extends JpaRepository<Obstacle, Integer> {
    List<Obstacle> findByIsDeleteFalseOrderByDetectionTimeDesc();
    List<Obstacle> findByCameraIdAndIsDeleteFalseOrderByDetectionTimeDesc(Long cameraId);
}