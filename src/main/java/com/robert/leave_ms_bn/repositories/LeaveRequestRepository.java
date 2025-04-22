package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
}