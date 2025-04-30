package com.robert.leave_ms_bn.controllers.notification;

import com.robert.leave_ms_bn.dtos.notifications.NotificationDto;
import com.robert.leave_ms_bn.entities.Notification;
import com.robert.leave_ms_bn.mappers.NotificationMapper;
import com.robert.leave_ms_bn.repositories.NotificationRepository;
import com.robert.leave_ms_bn.repositories.NotificationTypeRepository;
import com.robert.leave_ms_bn.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationsController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationTypeRepository notificationTypeRepository;

    // Get all notifications, optionally filter by user_id
    @GetMapping
    public List<Notification> getAllNotificationsByUserId(@RequestParam(required = false) Long user_id) {
        List<Notification> notifications;

        if (user_id != null) {
            // Fetch notifications for a specific user by user_id
            notifications = notificationRepository.findByUserId(user_id).stream()
//                    .map(notificationMapper::toNotificationDto)
                    .collect(Collectors.toList()).reversed();
        } else {
            // Fetch all notifications if no user_id is provided
            notifications = notificationRepository.findAll().stream()
//                    .map(notificationMapper::toNotificationDto)
                    .collect(Collectors.toList());
        }

        return notifications;
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

}
