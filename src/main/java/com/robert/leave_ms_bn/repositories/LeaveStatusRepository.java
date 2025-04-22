package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveStatusRepository extends JpaRepository<LeaveStatus, Integer> {
}