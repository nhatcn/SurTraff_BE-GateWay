package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "camera")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String location;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String streamUrl;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    private String status = "true";

    private LocalDateTime createdAt = LocalDateTime.now();
}
