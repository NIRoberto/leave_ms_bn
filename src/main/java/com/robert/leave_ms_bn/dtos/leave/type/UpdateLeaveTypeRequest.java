package com.robert.leave_ms_bn.dtos.leave.type;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateLeaveTypeRequest {
    private String name;
    private String description;
    private int max_days_per_year;
    private boolean is_paid;
}
