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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "violation_id")
    private Violation violation;

    @ManyToOne
    @JoinColumn(name = "violation_type_id")
    private ViolationType violationType;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "violation_time")
    private LocalDateTime violationTime;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "additional_notes")
    private String additionalNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
