package com.robert.leave_ms_bn.dtos.role;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleRequest {
    private String name;
    private String description;

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RoleDto {

        private int id;

        private String name;

        private String description;

        // If you need the timestamps as Date or Instant, you can uncomment these lines.
        // You might want to return them as a `Date` or `Instant`, depending on your use case.
        private Instant created_at; // Or use Date if you need to serialize to a more compatible format with front-end
        private Instant updated_at;

    }
}
