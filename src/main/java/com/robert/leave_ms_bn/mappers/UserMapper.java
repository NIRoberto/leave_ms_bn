package com.robert.leave_ms_bn.mappers;

import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.dtos.users.UserDto;
import com.robert.leave_ms_bn.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {RoleMapper.class, UserMapper.class})

public interface UserMapper {

    @Mapping(target = "roleId" ,source = "role.id")
    UserDto toUserDto(User user);

    User registerUser(RegisterRequest request); // Mapping RegisterRequest to User entity
}

