package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "violation_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "violation_id", nullable = false)
    private Violation violation;

    @ManyToOne
    @JoinColumn(name = "violation_type_id")
    private ViolationType violationType;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String videoUrl;

    private String location;
    private LocalDateTime violationTime;
    private Double speed;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    private LocalDateTime createdAt = LocalDateTime.now();
}
