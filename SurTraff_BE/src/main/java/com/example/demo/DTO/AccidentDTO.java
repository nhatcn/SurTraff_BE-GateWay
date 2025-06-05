package com.example.demo.DTO;

import java.util.Date;

public class AccidentDTO {
    private Long id;
    private Long camera_id;
    private String name;
    private String description;
    private  String image_url;
    private  String video_url;
    private String location;
    private Date accident_time;
    private Date created_at;
    public AccidentDTO(){
    }

    public AccidentDTO(Long accident_id, Long camera_id, String name, String description, String image_url, String video_url, String location, Date accident_time, Date created_at) {
        this.id = accident_id;
        this.camera_id = camera_id;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.video_url = video_url;
        this.location = location;
        this.accident_time = accident_time;
        this.created_at = created_at;
    }

    public Long getAccident_id() {
        return id;
    }

    public void setAccident_id(Long accident_id) {
        this.id = accident_id;
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
