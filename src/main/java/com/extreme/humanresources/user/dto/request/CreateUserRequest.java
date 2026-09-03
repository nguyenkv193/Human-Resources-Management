package com.extreme.humanresources.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 6, max = 100, message = "Username phải từ 6 đến 100 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Mật khẩu phải có chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    private String password;

    private Long employeeId;

    @NotEmpty(message = "User phải có ít nhất một role")
    private Set<Long> roleIds = new HashSet<>();
}
