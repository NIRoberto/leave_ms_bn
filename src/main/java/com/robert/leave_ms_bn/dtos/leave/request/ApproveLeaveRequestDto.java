package com.robert.leave_ms_bn.dtos.leave.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveLeaveRequestDto {
    private Long requestId;
    private Long reviewerById;
    private int leaveStatusId;
    private String comment;
}
