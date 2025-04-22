package com.robert.leave_ms_bn.mappers.leave;


import com.robert.leave_ms_bn.dtos.leave.LeaveStatusDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveStatusType {

    LeaveStatusDto toDto(LeaveStatusType leaveStatusType);
    LeaveStatusType fromDto(LeaveStatusType leaveStatusType);
    LeaveStatusType CreateLeaveStatusType(LeaveStatusDto leaveStatusDto);
    LeaveStatusType UpdateLeaveStatusType(LeaveStatusDto leaveStatusDto);

}

