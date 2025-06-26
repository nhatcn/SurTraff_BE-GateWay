package com.example.demo.DTO;

import java.time.LocalDateTime;
import java.util.Date;

public class ViolationDTO {
    private Long id;
    private Long camera_id;
    private Long vehicle_id;
    private Long user_id;
    private String licensePlate;
    private Long violation_type_id;
    private String type_name;
    private LocalDateTime created_at;
    private String location;


    public ViolationDTO() {
    }

    public ViolationDTO(Long id, Long camera_id, Long vehicle_id, Long user_id, String licensePlate, Long violation_type_id, String type_name, LocalDateTime created_at, String location) {
        this.id = id;
        this.camera_id = camera_id;
        this.vehicle_id = vehicle_id;
        this.user_id = user_id;
        this.licensePlate = licensePlate;
        this.violation_type_id = violation_type_id;
        this.type_name = type_name;
        this.created_at = created_at;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Long getViolation_type_id() {
        return violation_type_id;
    }

    public void setViolation_type_id(Long violation_type_id) {
        this.violation_type_id = violation_type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(Long camera_id) {
        this.camera_id = camera_id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
