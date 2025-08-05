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
    private Integer id; // Changed to Integer to match database
    private CameraDTO camera;
    private VehicleType vehicleType;
    private VehicleDTO vehicle;
    private LocalDateTime createdAt;
    private List<ViolationDetailDTO> violationDetails;
    private String status; // Should match varchar(50) if needed, but length is not enforced in DTO
    private Boolean isDelete; // Added to match database structure
}