package com.robert.leave_ms_bn.services;

import com.robert.leave_ms_bn.dtos.notifications.NotificationDto;
import com.robert.leave_ms_bn.dtos.notifications.create.SendNotificationDto;
import com.robert.leave_ms_bn.entities.Notification;
import com.robert.leave_ms_bn.entities.NotificationType;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.mappers.NotificationMapper;
import com.robert.leave_ms_bn.repositories.NotificationRepository;
import com.robert.leave_ms_bn.repositories.NotificationTypeRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationDto sendNotification(SendNotificationDto dto) {
        User user = userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationType type = notificationTypeRepository.findById(dto.getNotification_type_id())
                .orElseThrow(() -> new RuntimeException("Notification type not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setMessage(dto.getMessage());
        notification.setIsRead(dto.is_read());
        notification.setCreatedAt(dto.getCreated_at());

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toNotificationDto(saved);
    }

    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return notificationMapper.toNotificationDto(notification);
    }
}
