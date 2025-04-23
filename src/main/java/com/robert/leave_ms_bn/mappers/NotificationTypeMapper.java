package com.robert.leave_ms_bn.mappers;

import com.robert.leave_ms_bn.dtos.notifications.NotificationTypeDto;
import com.robert.leave_ms_bn.dtos.notifications.type.CreateNotificationType;
import com.robert.leave_ms_bn.dtos.notifications.type.UpdateNotificationType;
import com.robert.leave_ms_bn.entities.NotificationType;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))

public interface NotificationTypeMapper {

//    @Mapping(target = "id", source = "id")


    NotificationTypeDto toEntity( NotificationType dto);

    NotificationType creaNotificationType( CreateNotificationType dto);
    NotificationType updateNotificationType( UpdateNotificationType dto);
}


