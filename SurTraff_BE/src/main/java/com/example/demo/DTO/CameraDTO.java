package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CameraDTO {
    private Long id;
    private String name;
    private String location;
    private String streamUrl;
    private String thumbnail;
    private Long zoneId;
    private Double latitude;
    private Double longitude;
}
