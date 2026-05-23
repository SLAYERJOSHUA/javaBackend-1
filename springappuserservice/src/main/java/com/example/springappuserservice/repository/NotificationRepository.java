package com.example.springappuserservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappuserservice.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);
}
