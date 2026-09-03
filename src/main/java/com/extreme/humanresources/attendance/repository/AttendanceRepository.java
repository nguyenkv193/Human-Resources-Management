package com.extreme.humanresources.attendance.repository;

import com.extreme.humanresources.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.position"})
    List<AttendanceRecord> findAllByOrderByWorkDateDescEmployeeFullNameAsc();

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.position"})
    List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.position"})
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    long countByWorkDate(LocalDate workDate);
}
