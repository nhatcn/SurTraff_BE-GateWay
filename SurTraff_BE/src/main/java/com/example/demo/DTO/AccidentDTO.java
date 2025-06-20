package com.example.demo.DTO;

import java.util.Date;

public class AccidentDTO {
    private Long id;
    private Long camera_id;
    private Long vehicle_id;
    private Long user_id;
    private String user_email;
    private String user_fullName;
    private String licensePlate;
    private String name;
    private String description;
    private String image_url;
    private String video_url;
    private String location;
    private String status;
    private Date accident_time;
    private Date created_at;

    public AccidentDTO() {
    }

    public AccidentDTO(Long id, Long camera_id, Long vehicle_id, Long user_id, String user_email, String user_fullName, String licensePlate, String name, String description, String image_url, String video_url, String location, String status, Date accident_time, Date created_at) {
        this.id = id;
        this.camera_id = camera_id;
        this.vehicle_id = vehicle_id;
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_fullName = user_fullName;
        this.licensePlate = licensePlate;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.video_url = video_url;
        this.location = location;
        this.status = status;
        this.accident_time = accident_time;
        this.created_at = created_at;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getUser_fullName() {
        return user_fullName;
    }

    public void setUser_fullName(String fullName) {
        this.user_fullName = fullName;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String email) {
        this.user_email = email;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(Long vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public Long getCamera_id() {
        return camera_id;
    }

    public void setCamera_id(Long camera_id) {
        this.camera_id = camera_id;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
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

    public Date getAccident_time() {
        return accident_time;
    }

    public void setAccident_time(Date accident_time) {
        this.accident_time = accident_time;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
