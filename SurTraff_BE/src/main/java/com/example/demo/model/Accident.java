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

    private String image_url;

    private String description;

    private String video_url;

    private String location;

    private LocalDateTime accident_time;
    private LocalDateTime created_at = LocalDateTime.now();
}