package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String streamUrl;
    private String thumbnail;
    private Double latitude;
    private Double longitude;
    private Boolean status;
    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zone> zones;
}
