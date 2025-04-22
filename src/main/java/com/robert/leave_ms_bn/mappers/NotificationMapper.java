package com.robert.leave_ms_bn.mappers;


import com.robert.leave_ms_bn.dtos.notifications.NotificationDto;
import com.robert.leave_ms_bn.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "user_id" , source = "user.id")
    @Mapping(target = "is_read" , source = "isRead")

    NotificationDto toNotificationDto(Notification notification);
    Notification toNotification(NotificationDto notificationDto);

}
