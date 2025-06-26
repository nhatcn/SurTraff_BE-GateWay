package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties("accidents")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnoreProperties("accidents")
    private Vehicle vehicle;

    private String image_url;

    private String description;

    private String video_url;

    private String location;

    private String status;

    private LocalDateTime accident_time;
    private LocalDateTime created_at = LocalDateTime.now();
}