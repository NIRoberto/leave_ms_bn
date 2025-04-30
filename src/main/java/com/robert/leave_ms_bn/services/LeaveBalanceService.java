package com.robert.leave_ms_bn.services;

import com.robert.leave_ms_bn.dtos.leave.LeaveBalanceDto;
import com.robert.leave_ms_bn.entities.LeaveBalance;
import com.robert.leave_ms_bn.entities.LeaveType;
import com.robert.leave_ms_bn.exceptions.LeaveBalanceNotFoundException;
import com.robert.leave_ms_bn.repositories.LeaveBalanceRepository;
import com.robert.leave_ms_bn.mappers.LeaveBalanceMapper;
import com.robert.leave_ms_bn.repositories.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;

    // Get all leave balances
    public List<LeaveBalanceDto> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll()
                .stream()
                .map(leaveBalanceMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get leave balances by employee ID
    public List<LeaveBalance> getLeaveBalancesByEmployeeId(Long employeeId) {
        return leaveBalanceRepository.findByEmployeeId(employeeId).stream()
//                .map(leaveBalanceMapper::toDto)
                .collect(Collectors.toList()).reversed();
    }
    // Get leave balance by employee ID and leave type ID
    public LeaveBalance getLeaveBalanceByEmployeeAndType(Long employeeId, int leaveTypeId) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseGet(() -> {
                    LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                            .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + leaveTypeId));

                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setEmployeeId(employeeId);
                    newBalance.setLeaveType(leaveType);
                    newBalance.setTotalDays(leaveType.getMaxDaysPerYear());
                    newBalance.setUsedDays(0);
                    newBalance.setYear(Instant.now().atZone(ZoneId.systemDefault()).getYear());
                    newBalance.setCreatedAt(Instant.now());
                    newBalance.setUpdatedAt(Instant.now());

                    return leaveBalanceRepository.save(newBalance);
                });
    }



    public LeaveBalanceDto createOrUpdateLeaveBalance(Long employeeId, int leaveTypeId, int days) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + leaveTypeId));

        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseGet(() -> {
                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setEmployeeId(employeeId);
                    newBalance.setLeaveType(leaveType);
                    newBalance.setTotalDays(0);
                    newBalance.setUsedDays(0); // Default to 0
                    newBalance.setYear(Instant.now().atZone(ZoneId.systemDefault()).getYear());
                    return newBalance;
                });

        // Calculate available leave days
        int availableDays = leaveBalance.getTotalDays() - leaveBalance.getUsedDays();

        // Validate if requested days exceed available leave days
        if (days > availableDays) {
            throw new RuntimeException(
                    String.format("Insufficient leave balance. Requested: %d, Available: %d", days, availableDays)
            );
        }

        // Update used days with the actual leave request
        leaveBalance.setUsedDays(leaveBalance.getUsedDays() + days);
        leaveBalance.setUpdatedAt(Instant.now());

        // Optionally update total days if necessary (e.g., based on leave type's maxDaysPerYear)
        leaveBalance.setTotalDays(leaveBalance.getTotalDays() + leaveType.getMaxDaysPerYear());

        LeaveBalance savedBalance = leaveBalanceRepository.save(leaveBalance);
        return leaveBalanceMapper.toDto(savedBalance);
    }


    // Check if there is an available leave slot for a member
    public boolean hasAvailableLeaveSlot(Long employeeId, int leaveTypeId) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .map(leaveBalance -> (leaveBalance.getTotalDays() - leaveBalance.getUsedDays()) > 0)
                .orElse(false);
    }
}