package com.example.demo.DTO;

import com.example.demo.model.Zone;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneDTO {
    private Long id;
    private String name;
    private Long cameraId;
    private String zoneType; // lane, light, line
    private String coordinates;


    public Zone toEntity() {
        return Zone.builder()
                .id(this.id)
                .name(this.name)
                .coordinates(this.coordinates)

                .build();
    }
}
