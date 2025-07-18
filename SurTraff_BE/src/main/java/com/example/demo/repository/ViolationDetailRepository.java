package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.ViolationDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ViolationDetailRepository extends JpaRepository<ViolationDetail, Long> {
    @Modifying
    @Query("DELETE FROM ViolationDetail vd WHERE vd.violation.id = :violationId")
    void deleteByViolationId(Long violationId);
}