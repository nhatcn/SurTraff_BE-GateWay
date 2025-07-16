package com.example.demo.DTO;

import java.time.LocalDateTime;

public class ViolationDTO {
    private Long id;
    private Long camera_id;
    private Long vehicle_type_id;
    private Long vehicle_id;
    private LocalDateTime created_at;
    private String licensePlate;
    private String location;

    public ViolationDTO() {
    }

    public ViolationDTO(Long id, Long camera_id, Long vehicle_type_id, Long vehicle_id, LocalDateTime created_at, String licensePlate, String location) {
        this.id = id;
        this.camera_id = camera_id;
        this.vehicle_type_id = vehicle_type_id;
        this.vehicle_id = vehicle_id;
        this.created_at = created_at;
        this.licensePlate = licensePlate;
        this.location = location;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
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

    public Long getVehicle_type_id() {
        return vehicle_type_id;
    }

    public void setVehicle_type_id(Long vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}