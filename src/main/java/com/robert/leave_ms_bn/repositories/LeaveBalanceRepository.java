package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.LeaveBalance;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
  List<LeaveBalance> findByEmployeeId(Long employeeId);

  Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, int leaveTypeId);


}