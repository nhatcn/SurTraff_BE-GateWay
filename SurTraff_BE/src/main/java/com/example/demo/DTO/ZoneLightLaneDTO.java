package com.example.demo.DTO;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneLightLaneDTO {
    private Long lightZoneId;
    private Long laneZoneId;
}
