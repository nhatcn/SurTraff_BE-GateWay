package com.example.demo.DTO;

import java.util.Date;

public class AccidentDTO {
    private Long id;
    private Long cameraId;
    private Double latitude;
    private Double longitude;
    private Long vehicleId;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String licensePlate;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String location;
    private String status;
    private Date accidentTime;
    private Date createdAt;

    public AccidentDTO() {
    }

    public AccidentDTO(Long id, Long cameraId, Double latitude, Double longitude, Long vehicleId, Long userId, String userEmail, String userFullName, String licensePlate, String name, String description, String imageUrl, String videoUrl, String location, String status, Date accidentTime, Date createdAt) {
        this.id = id;
        this.cameraId = cameraId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.licensePlate = licensePlate;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.location = location;
        this.status = status;
        this.accidentTime = accidentTime;
        this.createdAt = createdAt;
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAccidentTime() {
        return accidentTime;
    }

    public void setAccidentTime(Date accidentTime) {
        this.accidentTime = accidentTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
