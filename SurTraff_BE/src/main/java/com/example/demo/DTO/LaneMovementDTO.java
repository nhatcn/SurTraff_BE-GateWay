package com.example.demo.DTO;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaneMovementDTO {
    private Long fromLaneZoneId;
    private Long toLaneZoneId;
}
