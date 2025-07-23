package com.example.demo.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CameraWithZonesDTO {
    private Long id;
    private String name;
    private String streamUrl;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer maxSpeed;
    private Integer violationTypeId;
    private String thumbnail;
    private List<ZoneDTO> zones;
    private List<ZoneLightLaneDTO> zoneLightLaneLinks;
    private List<LaneMovementDTO> laneMovements;
}
