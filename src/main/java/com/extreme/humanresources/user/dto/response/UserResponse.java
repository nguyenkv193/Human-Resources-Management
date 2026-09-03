package com.extreme.humanresources.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private boolean enabled;
    private Long employeeId;
    private Set<RoleResponse> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
