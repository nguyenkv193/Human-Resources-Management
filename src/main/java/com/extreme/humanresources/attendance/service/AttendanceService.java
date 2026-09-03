package com.extreme.humanresources.attendance.service;

import com.extreme.humanresources.attendance.entity.AttendanceRecord;
import com.extreme.humanresources.attendance.entity.AttendanceStatus;
import com.extreme.humanresources.attendance.repository.AttendanceRepository;
import com.extreme.humanresources.common.exception.ResourceNotFoundException;
import com.extreme.humanresources.employee.entity.Employee;
import com.extreme.humanresources.employee.repository.EmployeeRepository;
import com.extreme.humanresources.user.entity.User;
import com.extreme.humanresources.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private static final LocalTime LATE_AFTER = LocalTime.of(8, 15);

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public List<AttendanceRecord> findAll() {
        return attendanceRepository.findAllByOrderByWorkDateDescEmployeeFullNameAsc();
    }

    public List<AttendanceRecord> findByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
    }

    public Optional<Employee> findCurrentEmployee(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(User::getEmployeeId)
                .flatMap(employeeRepository::findById);
    }

    public Optional<AttendanceRecord> findToday(String username) {
        return findCurrentEmployee(username)
                .flatMap(employee -> attendanceRepository.findByEmployeeIdAndWorkDate(employee.getId(), LocalDate.now()));
    }

    @Transactional
    public AttendanceRecord checkIn(String username) {
        Employee employee = requireCurrentEmployee(username);
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRepository.findByEmployeeIdAndWorkDate(employee.getId(), today)
                .orElseGet(() -> AttendanceRecord.builder()
                        .employee(employee)
                        .workDate(today)
                        .build());

        if (record.getCheckIn() != null) {
            throw new IllegalStateException("Bạn đã check-in hôm nay");
        }

        LocalTime now = LocalTime.now().withNano(0);
        record.setCheckIn(now);
        record.setStatus(now.isAfter(LATE_AFTER) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);
        return attendanceRepository.save(record);
    }

    @Transactional
    public AttendanceRecord checkOut(String username) {
        Employee employee = requireCurrentEmployee(username);
        AttendanceRecord record = attendanceRepository.findByEmployeeIdAndWorkDate(employee.getId(), LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("Bạn chưa check-in hôm nay"));

        if (record.getCheckOut() != null) {
            throw new IllegalStateException("Bạn đã check-out hôm nay");
        }

        record.setCheckOut(LocalTime.now().withNano(0));
        return attendanceRepository.save(record);
    }

    private Employee requireCurrentEmployee(String username) {
        return findCurrentEmployee(username)
                .orElseThrow(() -> new IllegalStateException("Tài khoản chưa được liên kết với hồ sơ nhân viên"));
    }
}
