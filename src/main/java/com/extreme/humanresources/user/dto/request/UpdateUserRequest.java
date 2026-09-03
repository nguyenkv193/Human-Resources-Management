package com.extreme.humanresources.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 6, max = 100, message = "Username phải từ 6 đến 100 ký tự")
    private String username;

    @NotNull(message = "Trạng thái user không được để trống")
    private Boolean enabled;

    private Long employeeId;

    @NotEmpty(message = "User phải có ít nhất một role")
    private Set<Long> roleIds = new HashSet<>();
}
