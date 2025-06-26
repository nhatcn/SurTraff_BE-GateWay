package com.example.demo.service;

import com.example.demo.DTO.NotificationsDTO;
import com.example.demo.model.Notifications;
import com.example.demo.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationsService {
    @Autowired
    private NotificationsRepository notificationsRepository;

    public List<NotificationsDTO> getAllNotifications() {
        List<Notifications> notificationsList = notificationsRepository.findAll();

        return notificationsList.stream().map(n -> new NotificationsDTO(
                n.getId(),
                n.getUser() != null ? Long.valueOf(n.getUser().getId()) : null,
                n.getVehicle() != null ? Long.valueOf(n.getVehicle().getId()) : null,
                n.getAccident() != null ? Long.valueOf(n.getAccident().getId()) : null,
                n.getViolation() != null ? Long.valueOf(n.getViolation().getId()) : null,
                n.getMessage(),
                n.getNotification_type(),
                n.getCreated_at() != null ? Timestamp.valueOf(n.getCreated_at()) : null,
                n.isRead()
        )).collect(Collectors.toList());
    }

    public List<NotificationsDTO> getNotificationsByUserId(Long userId) {
        List<Notifications> notificationsList = notificationsRepository.findByUserId(userId);

        return notificationsList.stream().map(n -> new NotificationsDTO(
                n.getId(),
                n.getUser() != null ? Long.valueOf(n.getUser().getId()) : null,
                n.getVehicle() != null ? Long.valueOf(n.getVehicle().getId()) : null,
                n.getAccident() != null ? Long.valueOf(n.getAccident().getId()) : null,
                n.getViolation() != null ? Long.valueOf(n.getViolation().getId()) : null,
                n.getMessage(),
                n.getNotification_type(),
                n.getCreated_at() != null ? Timestamp.valueOf(n.getCreated_at()) : null,
                n.isRead()
        )).collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        System.out.println("markAsRead called with id: " + notificationId);
        Optional<Notifications> optionalNotification = notificationsRepository.findById(notificationId);

        if (optionalNotification.isPresent()) {
            Notifications notification = optionalNotification.get();
            notification.setRead(true);
            notificationsRepository.save(notification);
        } else {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }
    }
}
