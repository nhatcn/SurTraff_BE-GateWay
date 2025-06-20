package com.example.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationsDTO {
    private Long id;
    private String createdAt;
    private Long vehicleId; }

