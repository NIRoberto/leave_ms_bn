package com.robert.leave_ms_bn.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;

    private String first_name;

    private String last_name;

    private String email;

    private String phone;

    private Long roleId; // or use a RoleDto if you want to include more info about the role (name, description, etc.)

    private String password; // Only include this if necessary. You can exclude it in the response for security.

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant created_at;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant updated_at;

}
