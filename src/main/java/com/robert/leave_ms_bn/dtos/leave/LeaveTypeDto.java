package com.robert.leave_ms_bn.dtos.leave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class LeaveTypeDto {

    private int id;
     private String name;
     private String description;
     private int max_days_per_year;
     private boolean is_paid;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant created_at;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant updated_at;

}
