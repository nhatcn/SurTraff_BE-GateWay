package com.example.demo.repository;


import com.example.demo.model.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViolationTypeRepository extends JpaRepository<ViolationType, Long> {

    Optional<ViolationType> findById(Long id);
} 