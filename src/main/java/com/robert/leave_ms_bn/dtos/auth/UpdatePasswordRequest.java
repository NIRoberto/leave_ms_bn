package com.robert.leave_ms_bn.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
