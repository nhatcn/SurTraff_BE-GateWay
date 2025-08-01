package com.example.demo.repository;

import com.example.demo.model.Accident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, Long> {
    List<Accident> findByCameraId(Long cameraId);

    @Override
    List<Accident> findAll();

    List<Accident> findByVehicle_User_Id(Long userId);

    List<Accident> findByIsDeleteFalse();
    List<Accident> findByVehicle_User_IdAndIsDeleteFalse(Long userId);

}