package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_density")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrafficDensity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    private Integer vehicleCount;
    private LocalDateTime createdAt = LocalDateTime.now();
}