package com.extreme.humanresources.leave.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LeaveRequestForm {

    @NotNull(message = "Vui lòng chọn loại nghỉ")
    private Long leaveTypeId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @Size(max = 2000, message = "Lý do tối đa 2.000 ký tự")
    private String reason;
}
