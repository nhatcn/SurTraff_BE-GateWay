package com.example.demo.DTO;

import java.util.Date;

public class TrafficDensityDTO {
    private Long id;
    private Long cameraId;
    private String location;
    private Long vehicleCount;
    private Date createdAt;
    private Double latitude;
    private Double longitude;

    public TrafficDensityDTO() {
    }

    public TrafficDensityDTO(Long id, Long cameraId, String location, Long vehicleCount, Date createdAt, Double latitude, Double longitude) {
        this.id = id;
        this.cameraId = cameraId;
        this.location = location;
        this.vehicleCount = vehicleCount;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Long vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
