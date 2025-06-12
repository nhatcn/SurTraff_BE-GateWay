package com.example.demo.DTO;

public class ViolationsDTO {
    private Long id;
    private String createdAt;
    private Integer vehicleId; // Sửa từ Long thành Integer

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
}