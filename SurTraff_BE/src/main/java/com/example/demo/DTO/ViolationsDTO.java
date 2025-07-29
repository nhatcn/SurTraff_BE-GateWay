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
    private CameraDTO camera;
    private VehicleType vehicleType;
    private VehicleDTO vehicle;
    private LocalDateTime createdAt;
    private List<ViolationDetailDTO> violationDetails;
    private String status;
}
