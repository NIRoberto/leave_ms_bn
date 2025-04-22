package com.robert.leave_ms_bn.dtos.leave;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateLeaveStatus {
    private String name;
    private String label;
    private String color;
}


