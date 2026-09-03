package com.extreme.humanresources.leave.repository;

import com.extreme.humanresources.leave.entity.LeaveRequest;
import com.extreme.humanresources.leave.entity.LeaveRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approvedBy"})
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approvedBy"})
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    @EntityGraph(attributePaths = {"employee", "employee.department", "leaveType", "approvedBy"})
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(LeaveRequestStatus status);

    long countByStatus(LeaveRequestStatus status);
}
