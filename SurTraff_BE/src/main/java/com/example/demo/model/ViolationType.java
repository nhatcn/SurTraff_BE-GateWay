package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "violation_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ViolationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String typeName;

    @Column(columnDefinition = "TEXT")
    private String description;
}
