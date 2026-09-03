package com.extreme.humanresources.employee.dto.request;

import com.extreme.humanresources.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeForm {

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(max = 50, message = "Mã nhân viên tối đa 50 ký tự")
    private String employeeCode;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    private String email;

    @Size(max = 30, message = "Số điện thoại tối đa 30 ký tự")
    private String phone;

    @PastOrPresent(message = "Ngày sinh không thể ở tương lai")
    private LocalDate dateOfBirth;

    @Size(max = 30, message = "Giới tính tối đa 30 ký tự")
    private String gender;

    @Size(max = 2000, message = "Địa chỉ tối đa 2.000 ký tự")
    private String address;

    @NotNull(message = "Ngày vào làm không được để trống")
    private LocalDate hireDate;

    private LocalDate terminationDate;

    @NotNull(message = "Trạng thái không được để trống")
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    private Long departmentId;
    private Long positionId;
    private Long managerId;
}
