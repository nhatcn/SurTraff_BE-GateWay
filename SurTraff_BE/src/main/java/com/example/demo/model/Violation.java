package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
