package com.robert.leave_ms_bn.controllers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveBalanceDto;
import com.robert.leave_ms_bn.entities.LeaveBalance;
import com.robert.leave_ms_bn.services.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    // GET all leave balances
    @GetMapping
    public ResponseEntity<List<LeaveBalanceDto>> getAllLeaveBalances() {
        return ResponseEntity.ok(leaveBalanceService.getAllLeaveBalances());
    }

    // GET leave balances by employee ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalance>> getLeaveBalancesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalancesByEmployeeId(employeeId));
    }

    // Optionally, GET leave balance by employee & type
    @GetMapping("/employee/{employeeId}/type/{leaveTypeId}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceByType(
            @PathVariable Long employeeId,
            @PathVariable int leaveTypeId) {
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalanceByEmployeeAndType(employeeId, leaveTypeId));
    }
}
