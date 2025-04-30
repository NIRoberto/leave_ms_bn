package com.robert.leave_ms_bn.mappers.leave;

import com.robert.leave_ms_bn.dtos.leave.LeaveTypeDto;
import com.robert.leave_ms_bn.entities.LeaveType;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true) , uses = {LeaveTypeMapper.class})

public interface LeaveTypeMapper {

    @Mapping(target = "id", source = "id")



    LeaveTypeDto toDto(LeaveType leaveType);
    LeaveType toEntity(LeaveTypeDto leaveTypeDto);
    List<LeaveTypeDto> toDto(List<LeaveType> leaveTypes);

    LeaveType createLeaveType(LeaveTypeDto leaveTypeDto);
    LeaveType updateLeaveType(LeaveTypeDto leaveTypeDto);
}
