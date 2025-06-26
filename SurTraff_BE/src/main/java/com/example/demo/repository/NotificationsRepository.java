package com.example.demo.repository;

import com.example.demo.model.Accident;
import com.example.demo.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    @Override
    List<Notifications> findAll();

    List<Notifications> findByUserId(Long userId);

}
