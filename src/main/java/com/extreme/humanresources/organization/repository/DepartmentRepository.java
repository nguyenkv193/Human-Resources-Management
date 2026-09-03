package com.extreme.humanresources.organization.repository;

import com.extreme.humanresources.organization.entity.Department;
import com.extreme.humanresources.organization.entity.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();

    List<Department> findAllByStatusOrderByNameAsc(DepartmentStatus status);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
