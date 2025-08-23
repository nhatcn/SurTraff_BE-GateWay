package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "obstacles")
public class Obstacle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @Column(name = "obstacle_type", length = 100)
    private String obstacleType;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    private String location;

    @Column(name = "detection_time")
    private LocalDateTime detectionTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isDelete == null) {
            isDelete = false;
        }
    }
}