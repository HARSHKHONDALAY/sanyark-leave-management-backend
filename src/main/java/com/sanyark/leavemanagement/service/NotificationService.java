package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.NotificationResponse;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, NotificationType type, String title, String message);
    void notifyAllManagers(NotificationType type, String title, String message);
    List<NotificationResponse> getMyNotifications(Long userId);
    long getUnreadCount(Long userId);
    NotificationResponse markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
}