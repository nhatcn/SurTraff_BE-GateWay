package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lane_movements")
public class LaneMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_lane_zone_id", nullable = false)
    private Zone fromLaneZone;

    @ManyToOne
    @JoinColumn(name = "to_lane_zone_id", nullable = false)
    private Zone toLaneZone;
}
