package com.example.demo.repository;

import com.example.demo.model.LaneMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LaneMovementRepository extends JpaRepository<LaneMovement, Long> {
    List<LaneMovement> findByFromLaneZoneId(Long fromLaneZoneId);
    List<LaneMovement> findByToLaneZoneId(Long toLaneZoneId);
    void deleteByFromLaneZoneIdAndToLaneZoneId(Long fromLaneZoneId,Long  toLaneZoneId);
} 