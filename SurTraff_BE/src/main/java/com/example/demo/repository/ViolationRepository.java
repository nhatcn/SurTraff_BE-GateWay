package com.example.demo.repository;

import com.example.demo.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findByVehicleLicensePlateOrderByCreatedAtDesc(String licensePlate);

    @Query("SELECT v FROM Violation v LEFT JOIN FETCH v.vehicle LEFT JOIN FETCH v.camera LEFT JOIN FETCH v.vehicleType LEFT JOIN FETCH v.violationDetails WHERE 1=1")
    List<Violation> findAll();
}