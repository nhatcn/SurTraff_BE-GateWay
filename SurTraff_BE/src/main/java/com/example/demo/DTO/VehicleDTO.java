package com.example.demo.DTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;
    private String name;
    private String licensePlate;
    private Long userId;
    private Long vehicleTypeId;
    private String color;
    private String brand;
}