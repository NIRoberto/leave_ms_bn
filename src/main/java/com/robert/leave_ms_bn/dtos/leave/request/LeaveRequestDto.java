package com.robert.leave_ms_bn.dtos.leave.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class LeaveRequestDto {
    private Long userId;
    private Long leaveTypeId;
    private Long reviewerById;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant endDate;
    private int duration;
}
