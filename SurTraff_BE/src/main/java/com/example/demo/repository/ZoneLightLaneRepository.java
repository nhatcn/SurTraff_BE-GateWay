package com.example.demo.repository;

import com.example.demo.model.ZoneLightLane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ZoneLightLaneRepository extends JpaRepository<ZoneLightLane, Long> {
    List<ZoneLightLane> findByLaneZoneId(Long laneZoneId);
} 