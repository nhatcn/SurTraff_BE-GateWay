package com.example.demo.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationDetailDTO {
    private Long id;
    private Long violationId;
    private Long violationTypeId;
    private String imageUrl;
    private String videoUrl;
    private String location;
    private LocalDateTime violationTime;
    private Double speed;
    private String additionalNotes;
    private LocalDateTime createdAt;
}