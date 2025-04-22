package com.robert.leave_ms_bn.dtos.notifications.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarkNotificationAsReadDto {
    private boolean isRead = false;

}
