package com.example.demo.DTO;

import java.util.Date;

public class TrafficDensityDTO {
    private Long id;
    private Long camera_id;
    private String location;
    private Long vehicle_count;
    private Date created_at;
    private Double latitude;
    private Double longitude;

    public TrafficDensityDTO() {
    }

    public TrafficDensityDTO(Long id, Long camera_id, String location, Long vehicle_count, Date created_at, Double latitude, Double longitude) {
        this.id = id;
        this.camera_id = camera_id;
        this.location = location;
        this.vehicle_count = vehicle_count;
        this.created_at = created_at;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Long getVehicle_count() {
        return vehicle_count;
    }

    public void setVehicle_count(Long vehicle_count) {
        this.vehicle_count = vehicle_count;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
