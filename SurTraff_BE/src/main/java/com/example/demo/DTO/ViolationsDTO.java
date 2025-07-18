package com.example.demo.DTO;

import com.example.demo.model.VehicleType;
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
    private VehicleType vehicleType;
    private Long vehicleId;
    private LocalDateTime createdAt;
    private List<ViolationDetailDTO> violationDetails;
    private String status;
}