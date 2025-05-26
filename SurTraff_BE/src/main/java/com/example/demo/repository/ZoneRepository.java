package com.example.demo.repository;

import com.example.demo.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByCameraId(Long cameraId);
    List<Zone> findByZoneType(Zone.ZoneType zoneType);
    Optional<Zone> findTopByOrderByIdDesc();

}
