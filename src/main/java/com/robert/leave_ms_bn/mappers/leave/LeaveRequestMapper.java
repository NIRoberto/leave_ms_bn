package com.robert.leave_ms_bn.mappers.leave;


import com.robert.leave_ms_bn.dtos.leave.LeaveRequestDto;
import com.robert.leave_ms_bn.entities.LeaveRequest;
import com.robert.leave_ms_bn.mappers.UserMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),uses ={UserMapper.class,LeaveStatusMapper.class , LeaveTypeMapper.class} )

public interface LeaveRequestMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "leaveStatusId", source = "leaveStatus.id")
    @Mapping(target = "reviewerById", source = "user.id")
    LeaveRequestDto toEntity(  LeaveRequest leaveRequest);
    LeaveRequest fromEntity(LeaveRequestDto leaveRequest);
}
