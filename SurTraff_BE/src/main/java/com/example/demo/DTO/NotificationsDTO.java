package com.example.demo.DTO;

import java.util.Date;

public class NotificationsDTO {
    private Long id;
    private Long user_id;
    private Long vehicle_id;
    private Long accident_id;
    private Long violation_id;
    private String message;
    private String notification_type;
    private Date created_at;
    private boolean read;

    public NotificationsDTO() {
    }

    public NotificationsDTO(Long id, Long user_id, Long vehicle_id, Long accident_id, Long violation_id, String message, String notification_type, Date created_at, boolean read) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_id = vehicle_id;
        this.accident_id = accident_id;
        this.violation_id = violation_id;
        this.message = message;
        this.notification_type = notification_type;
        this.created_at = created_at;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Long getAccident_id() {
        return accident_id;
    }

    public void setAccident_id(Long accident_id) {
        this.accident_id = accident_id;
    }

    public Long getViolation_id() {
        return violation_id;
    }

    public void setViolation_id(Long violation_id) {
        this.violation_id = violation_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean is_read) {
        this.read = read;
    }
}
