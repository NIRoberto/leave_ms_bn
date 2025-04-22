package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
}