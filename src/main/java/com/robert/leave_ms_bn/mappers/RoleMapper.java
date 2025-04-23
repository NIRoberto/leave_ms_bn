package com.robert.leave_ms_bn.mappers;

import com.robert.leave_ms_bn.dtos.role.CreateRoleRequest;
import com.robert.leave_ms_bn.dtos.role.UpdateRoleRequest;
import com.robert.leave_ms_bn.entities.Role;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RoleMapper {

//    @Mapping(target = "id", source = "id")
    UpdateRoleRequest.RoleDto toRole(Role role);

    Role  toRoleEntity(CreateRoleRequest roleDto);
    Role  toUserRoleEntity(UpdateRoleRequest roleDto);

}
