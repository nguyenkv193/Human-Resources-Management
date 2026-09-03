package com.extreme.humanresources.organization.dto.request;

import com.extreme.humanresources.organization.entity.PositionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PositionForm {

    @NotBlank(message = "Mã vị trí không được để trống")
    @Size(max = 50, message = "Mã vị trí tối đa 50 ký tự")
    private String code;

    @NotBlank(message = "Tên vị trí không được để trống")
    @Size(max = 150, message = "Tên vị trí tối đa 150 ký tự")
    private String name;

    @Size(max = 2000, message = "Mô tả tối đa 2.000 ký tự")
    private String description;

    @NotNull(message = "Trạng thái không được để trống")
    private PositionStatus status = PositionStatus.ACTIVE;
}
