package com.extreme.humanresources.leave.repository;

import com.extreme.humanresources.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    List<LeaveType> findAllByStatusOrderByNameAsc(LeaveType.LeaveTypeStatus status);
}
