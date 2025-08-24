package com.example.demo.DTO;

import java.util.Date;

public class NotificationsDTO {
    private Long id;
    private Long userId;
    private Long vehicleId;
    private String licensePlate;
    private Long accidentId;
    private Long violationId;
    private String message;
    private String notificationType;
    private Date createdAt;
    private boolean read;

    public NotificationsDTO() {
    }

    public NotificationsDTO(Long id, Long userId, Long vehicleId, String licensePlate, Long accidentId, Long violationId, String message, String notificationType, Date createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.licensePlate = licensePlate;
        this.accidentId = accidentId;
        this.violationId = violationId;
        this.message = message;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getAccidentId() {
        return accidentId;
    }

    public void setAccidentId(Long accidentId) {
        this.accidentId = accidentId;
    }

    public Long getViolationId() {
        return violationId;
    }

    public void setViolationId(Long violationId) {
        this.violationId = violationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
