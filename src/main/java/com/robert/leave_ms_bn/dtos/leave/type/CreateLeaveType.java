package com.robert.leave_ms_bn.dtos.leave.type;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateLeaveType {
    private String name;
    private String description;
    private int max_days_per_year;
    private boolean is_paid;
}
