package com.robert.leave_ms_bn.dtos.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter

public class LoginRequest {

    @NotNull(message = "Email is Required")
    private String email;

    @NotNull(message = "Password is Required")
    private String password;

}
