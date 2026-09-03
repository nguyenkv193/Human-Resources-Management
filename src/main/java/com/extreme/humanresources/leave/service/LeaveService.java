package com.extreme.humanresources.leave.service;

import com.extreme.humanresources.common.exception.ResourceNotFoundException;
import com.extreme.humanresources.employee.entity.Employee;
import com.extreme.humanresources.employee.repository.EmployeeRepository;
import com.extreme.humanresources.leave.dto.request.LeaveRequestForm;
import com.extreme.humanresources.leave.entity.LeaveRequest;
import com.extreme.humanresources.leave.entity.LeaveRequestStatus;
import com.extreme.humanresources.leave.entity.LeaveType;
import com.extreme.humanresources.leave.repository.LeaveRequestRepository;
import com.extreme.humanresources.leave.repository.LeaveTypeRepository;
import com.extreme.humanresources.user.entity.User;
import com.extreme.humanresources.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<LeaveRequest> findByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    public List<LeaveType> findActiveTypes() {
        return leaveTypeRepository.findAllByStatusOrderByNameAsc(LeaveType.LeaveTypeStatus.ACTIVE);
    }

    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn nghỉ phép #" + id));
    }

    public Employee findCurrentEmployee(String username) {
        return findCurrentEmployeeOptional(username)
                .orElseThrow(() -> new IllegalStateException("Tài khoản chưa được liên kết với hồ sơ nhân viên"));
    }

    public Optional<Employee> findCurrentEmployeeOptional(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(User::getEmployeeId)
                .flatMap(employeeRepository::findById);
    }

    @Transactional
    public LeaveRequest create(String username, LeaveRequestForm form) {
        Employee employee = findCurrentEmployee(username);
        LeaveType leaveType = leaveTypeRepository.findById(form.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ phép #" + form.getLeaveTypeId()));

        validateDates(form.getStartDate(), form.getEndDate());
        BigDecimal totalDays = calculateWorkingDays(form.getStartDate(), form.getEndDate());
        if (totalDays.signum() == 0) {
            throw new IllegalArgumentException("Khoảng thời gian không có ngày làm việc");
        }

        boolean overlaps = leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId()).stream()
                .filter(request -> request.getStatus() == LeaveRequestStatus.PENDING
                        || request.getStatus() == LeaveRequestStatus.APPROVED)
                .anyMatch(request -> !form.getEndDate().isBefore(request.getStartDate())
                        && !form.getStartDate().isAfter(request.getEndDate()));
        if (overlaps) {
            throw new IllegalArgumentException("Khoảng thời gian nghỉ đang trùng với một đơn khác");
        }

        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .totalDays(totalDays)
                .reason(normalizeNullable(form.getReason()))
                .status(LeaveRequestStatus.PENDING)
                .build();
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest approve(Long id, String approverUsername) {
        LeaveRequest request = requirePending(id);
        request.setStatus(LeaveRequestStatus.APPROVED);
        request.setApprovedAt(Instant.now());
        request.setApprovedBy(findCurrentEmployeeOptional(approverUsername).orElse(null));
        request.setRejectionReason(null);
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest reject(Long id, String approverUsername, String reason) {
        LeaveRequest request = requirePending(id);
        request.setStatus(LeaveRequestStatus.REJECTED);
        request.setApprovedAt(Instant.now());
        request.setApprovedBy(findCurrentEmployeeOptional(approverUsername).orElse(null));
        request.setRejectionReason(normalizeNullable(reason));
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest cancel(Long id, String username) {
        LeaveRequest request = findById(id);
        Employee employee = findCurrentEmployee(username);
        if (!request.getEmployee().getId().equals(employee.getId())) {
            throw new IllegalArgumentException("Bạn chỉ có thể hủy đơn nghỉ của chính mình");
        }
        if (request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể hủy đơn đang chờ duyệt");
        }
        request.setStatus(LeaveRequestStatus.CANCELLED);
        return leaveRequestRepository.save(request);
    }

    private LeaveRequest requirePending(Long id) {
        LeaveRequest request = findById(id);
        if (request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new IllegalStateException("Đơn nghỉ này không còn ở trạng thái chờ duyệt");
        }
        return request;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
    }

    private BigDecimal calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        long days = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                days++;
            }
        }
        return BigDecimal.valueOf(days);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
