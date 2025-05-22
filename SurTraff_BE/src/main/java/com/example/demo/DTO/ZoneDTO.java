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
    private String zoneType; // "line", "lane", "light"
    private String coordinates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Zone toEntity() {
        return Zone.builder()
                .id(this.id)
                .name(this.name)
                .coordinates(this.coordinates)
                .zoneType(Zone.ZoneType.valueOf(this.zoneType))
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
