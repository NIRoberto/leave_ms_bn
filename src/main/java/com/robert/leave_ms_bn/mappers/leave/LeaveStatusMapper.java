package com.robert.leave_ms_bn.mappers.leave;


import com.robert.leave_ms_bn.dtos.leave.LeaveStatusDto;
import com.robert.leave_ms_bn.entities.LeaveStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeaveStatusMapper {


    @Mapping(target = "id", source = "id")

    LeaveStatusDto toDto(LeaveStatus leaveStatusType);
    LeaveStatus fromDto(LeaveStatus leaveStatusType);
    LeaveStatus CreateLeaveStatus(LeaveStatusDto leaveStatusDto);
    LeaveStatus UpdateLeaveStatus(LeaveStatusDto leaveStatusDto);

}

