package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "obstacles")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Obstacle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    private String obstacleType;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String location;

    private LocalDateTime detectionTime;
    private LocalDateTime createdAt = LocalDateTime.now();
}