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
    private List<ZoneDTO> zones;
    private String thumbnail;
    private List<ZoneLightLaneDTO> zoneLightLaneLinks;
    private List<LaneMovementDTO> laneMovements;
}
