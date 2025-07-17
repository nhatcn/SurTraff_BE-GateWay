package com.example.demo.DTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationsDTO {
    private Long id;
    private Long cameraId;
    private Long vehicleTypeId;
    private Long vehicleId;
    private LocalDateTime createdAt;
    private List<ViolationDetailDTO> violationDetails;
}