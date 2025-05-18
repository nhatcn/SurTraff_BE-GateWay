package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accidents")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Accident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    private String description;

    private String videoUrl;

    private String location;

    private LocalDateTime accidentTime;
    private LocalDateTime createdAt = LocalDateTime.now();
}