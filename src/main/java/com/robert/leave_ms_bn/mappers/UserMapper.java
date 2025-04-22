package com.robert.leave_ms_bn.mappers;

import com.robert.leave_ms_bn.dtos.auth.RegisterRequest;
import com.robert.leave_ms_bn.dtos.users.UserDto;
import com.robert.leave_ms_bn.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    @Mapping(target = "roleId", source = "role.id") // Mapping role to roleId
    UserDto toUserDto(User user);

    User registerUser(RegisterRequest request); // Mapping RegisterRequest to User entity
}
