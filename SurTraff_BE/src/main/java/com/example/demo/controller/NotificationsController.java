package com.example.demo.controller;

import com.example.demo.DTO.NotificationsDTO;
import com.example.demo.service.NotificationsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;

    @GetMapping
    public ResponseEntity<List<NotificationsDTO>> getAllNotifications() {
        List<NotificationsDTO> notificationsDTOList = notificationsService.getAllNotifications();
        return ResponseEntity.ok(notificationsDTOList);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationsDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationsService.getNotificationsByUserId(userId));
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationsService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marked as read.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
