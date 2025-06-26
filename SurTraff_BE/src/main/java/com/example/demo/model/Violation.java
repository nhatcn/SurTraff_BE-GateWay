package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "violations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "violation_type_id")
    private ViolationType violationType;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "violation", cascade = CascadeType.ALL)
    private List<ViolationDetail> violationDetails;
}
