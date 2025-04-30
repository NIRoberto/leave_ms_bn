package com.robert.leave_ms_bn.dtos.leave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDto {

    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private String leaveTypeName;

    private Integer year;

    private Integer totalDays;

    private Integer usedDays;

    private Integer remainingDays;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
