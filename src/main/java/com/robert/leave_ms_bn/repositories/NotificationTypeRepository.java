package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {
}