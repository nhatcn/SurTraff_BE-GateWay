package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "accident_id", nullable = true)
    private Accident accident;

    @ManyToOne
    @JoinColumn(name = "violation_id", nullable = true) // hoặc bỏ hẳn nullable
    private Violation violation;

    private String message;

    private String notification_type;

    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "is_read")
    private boolean read;
}
