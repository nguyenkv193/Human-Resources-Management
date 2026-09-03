package com.extreme.humanresources.organization.dto.request;

import com.extreme.humanresources.organization.entity.DepartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentForm {

    @NotBlank(message = "Mã phòng ban không được để trống")
    @Size(max = 50, message = "Mã phòng ban tối đa 50 ký tự")
    private String code;

    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(max = 150, message = "Tên phòng ban tối đa 150 ký tự")
    private String name;

    @Size(max = 2000, message = "Mô tả tối đa 2.000 ký tự")
    private String description;

    private Long parentId;

    @NotNull(message = "Trạng thái không được để trống")
    private DepartmentStatus status = DepartmentStatus.ACTIVE;
}
