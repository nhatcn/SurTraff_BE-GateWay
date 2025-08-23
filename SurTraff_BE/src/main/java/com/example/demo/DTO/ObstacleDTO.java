package com.example.demo.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ObstacleDTO {
    private Integer id;
    private Integer cameraId;
    private String cameraName;
    private String obstacleType;
    private String imageUrl;
    private String location;
    private LocalDateTime detectionTime;
    private LocalDateTime createdAt;
    private Double latitude;
    private Double longitude;
}