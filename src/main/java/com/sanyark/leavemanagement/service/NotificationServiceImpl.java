package com.sanyark.leavemanagement.service;

import com.sanyark.leavemanagement.dto.NotificationResponse;
import com.sanyark.leavemanagement.entity.Notification;
import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.NotificationType;
import com.sanyark.leavemanagement.enums.Role;
import com.sanyark.leavemanagement.exception.ResourceNotFoundException;
import com.sanyark.leavemanagement.exception.UnauthorizedActionException;
import com.sanyark.leavemanagement.repository.NotificationRepository;
import com.sanyark.leavemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(User user, NotificationType type, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void notifyAllManagers(NotificationType type, String title, String message) {
        List<User> managers = userRepository.findByRole(Role.MANAGER);

        for (User manager : managers) {
            createNotification(manager, type, title, message);
        }
    }

    @Override
    public List<NotificationResponse> getMyNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You can only mark your own notifications as read");
        }

        notification.setRead(true);

        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().name())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}