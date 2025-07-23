package com.example.demo.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CameraSetupDTO {
    private String cameraName;
    private String cameraUrl;
    private Double latitude;
    private Double longitude;
    private Integer maxSpeed;
    private String location;
    private String thumbnail;
    private Long violationTypeId;
    private List<ZoneDTO> zones;
    private List<ZoneLightLaneDTO> zoneLightLaneLinks;
    private List<LaneMovementDTO> laneMovements;
}
