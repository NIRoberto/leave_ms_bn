package com.robert.leave_ms_bn.mappers;

import com.robert.leave_ms_bn.dtos.leave.LeaveBalanceDto;
import com.robert.leave_ms_bn.entities.LeaveBalance;
import com.robert.leave_ms_bn.mappers.leave.LeaveTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",   uses = {LeaveTypeMapper.class, UserMapper.class})
public interface LeaveBalanceMapper {

    LeaveBalanceMapper INSTANCE = Mappers.getMapper(LeaveBalanceMapper.class);

    // Map LeaveBalance entity to LeaveBalanceDto
//    @Mapping(source = "leaveType", target = "leaveTypeId")
    LeaveBalanceDto toDto(LeaveBalance leaveBalance);

    // Map LeaveBalanceDto to LeaveBalance entity

    LeaveBalance toEntity(LeaveBalanceDto leaveBalanceDto);
}