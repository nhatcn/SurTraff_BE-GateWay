package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "zone_light_lanes")
public class ZoneLightLane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "light_zone_id")
    private Zone lightZone;

    @ManyToOne
    @JoinColumn(name = "lane_zone_id")
    private Zone laneZone;
} 