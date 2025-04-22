package com.robert.leave_ms_bn.dtos.role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CreateRoleRequest {

    @NotNull(message = "Role name is required")
    private String name;
    private String description;


}
