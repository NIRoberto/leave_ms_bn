package com.robert.leave_ms_bn.dtos.leave;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private Long id;
    private Long userId;
    private int leaveTypeId;
    private Long reviewerById;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant endDate;
    private int duration;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant created_at;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant updated_at;
    private Long leaveStatusId;

    // Optionally include names to make frontend life easier
    private String userName;
    private String reviewerName;
    private String leaveTypeName;
    private String statusName;
}
