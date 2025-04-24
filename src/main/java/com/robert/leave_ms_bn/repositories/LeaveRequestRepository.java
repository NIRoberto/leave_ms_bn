package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
  List<LeaveRequest> findByUserId(Long userId);

  List<LeaveRequest> findAllByUserId(Long userId);
}