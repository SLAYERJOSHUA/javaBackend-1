package com.example.springappuserservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.springappuserservice.model.Notification;
import com.example.springappuserservice.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Notification notification) {
        notification.setId(null);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public Optional<Notification> markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setIsRead(true);
                    return notificationRepository.save(notification);
                });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = getUnreadNotifications(userId);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
}
