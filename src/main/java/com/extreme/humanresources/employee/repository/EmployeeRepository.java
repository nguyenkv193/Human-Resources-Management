package com.extreme.humanresources.employee.repository;

import com.extreme.humanresources.employee.entity.Employee;
import com.extreme.humanresources.employee.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @EntityGraph(attributePaths = {"department", "position", "manager"})
    @Query("""
            select e from Employee e
            left join e.department department
            left join e.position position
            where (:keyword = ''
                or lower(e.employeeCode) like lower(concat('%', :keyword, '%'))
                or lower(e.fullName) like lower(concat('%', :keyword, '%'))
                or lower(e.email) like lower(concat('%', :keyword, '%'))
                or lower(department.name) like lower(concat('%', :keyword, '%'))
                or lower(position.name) like lower(concat('%', :keyword, '%')))
              and (:status is null or e.status = :status)
            order by e.fullName asc
            """)
    List<Employee> search(@Param("keyword") String keyword,
                          @Param("status") EmployeeStatus status);

    @EntityGraph(attributePaths = {"department", "position", "manager"})
    List<Employee> findAllByOrderByFullNameAsc();

    @EntityGraph(attributePaths = {"department", "position", "manager"})
    Optional<Employee> findById(Long id);

    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCaseAndIdNot(String employeeCode, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByStatus(EmployeeStatus status);
}
