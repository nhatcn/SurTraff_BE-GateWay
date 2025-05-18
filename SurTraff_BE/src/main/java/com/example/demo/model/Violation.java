package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "violation_type_id")
    private ViolationType violationType;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    private String licensePlate;
    private String vehicleColor;
    private String vehicleBrand;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String videoUrl;

    private LocalDateTime violationTime;
    private LocalDateTime createdAt = LocalDateTime.now();
}