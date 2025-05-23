package com.example.demo.repository;

import com.example.demo.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findByLicensePlateOrderByViolationTimeDesc(String licensePlate);
}
