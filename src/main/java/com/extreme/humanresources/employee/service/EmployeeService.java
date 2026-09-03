package com.extreme.humanresources.employee.service;

import com.extreme.humanresources.common.exception.ResourceNotFoundException;
import com.extreme.humanresources.employee.dto.request.EmployeeForm;
import com.extreme.humanresources.employee.entity.Employee;
import com.extreme.humanresources.employee.entity.EmployeeStatus;
import com.extreme.humanresources.employee.repository.EmployeeRepository;
import com.extreme.humanresources.organization.entity.Department;
import com.extreme.humanresources.organization.entity.Position;
import com.extreme.humanresources.organization.repository.DepartmentRepository;
import com.extreme.humanresources.organization.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public List<Employee> findAll(String keyword, EmployeeStatus status) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        return employeeRepository.search(normalizedKeyword, status);
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên #" + id));
    }

    public EmployeeForm getForm(Long id) {
        Employee employee = findById(id);
        EmployeeForm form = new EmployeeForm();
        form.setEmployeeCode(employee.getEmployeeCode());
        form.setFullName(employee.getFullName());
        form.setEmail(employee.getEmail());
        form.setPhone(employee.getPhone());
        form.setDateOfBirth(employee.getDateOfBirth());
        form.setGender(employee.getGender());
        form.setAddress(employee.getAddress());
        form.setHireDate(employee.getHireDate());
        form.setTerminationDate(employee.getTerminationDate());
        form.setStatus(employee.getStatus());
        form.setDepartmentId(employee.getDepartment() == null ? null : employee.getDepartment().getId());
        form.setPositionId(employee.getPosition() == null ? null : employee.getPosition().getId());
        form.setManagerId(employee.getManager() == null ? null : employee.getManager().getId());
        return form;
    }

    public List<Department> findDepartments() {
        return departmentRepository.findAllByOrderByNameAsc();
    }

    public List<Position> findPositions() {
        return positionRepository.findAllByOrderByNameAsc();
    }

    public List<Employee> findManagers(Long excludedId) {
        return employeeRepository.findAllByOrderByFullNameAsc().stream()
                .filter(employee -> excludedId == null || !employee.getId().equals(excludedId))
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE)
                .toList();
    }

    @Transactional
    public Employee create(EmployeeForm form) {
        String employeeCode = normalize(form.getEmployeeCode());
        String email = normalize(form.getEmail());
        ensureUnique(employeeCode, email, null);
        validateDates(form);

        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .fullName(normalize(form.getFullName()))
                .email(email)
                .phone(normalizeNullable(form.getPhone()))
                .dateOfBirth(form.getDateOfBirth())
                .gender(normalizeNullable(form.getGender()))
                .address(normalizeNullable(form.getAddress()))
                .hireDate(form.getHireDate())
                .terminationDate(form.getTerminationDate())
                .status(form.getStatus())
                .department(resolveDepartment(form.getDepartmentId()))
                .position(resolvePosition(form.getPositionId()))
                .manager(resolveManager(form.getManagerId(), null))
                .build();

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, EmployeeForm form) {
        Employee employee = findById(id);
        String employeeCode = normalize(form.getEmployeeCode());
        String email = normalize(form.getEmail());
        ensureUnique(employeeCode, email, id);
        validateDates(form);

        employee.setEmployeeCode(employeeCode);
        employee.setFullName(normalize(form.getFullName()));
        employee.setEmail(email);
        employee.setPhone(normalizeNullable(form.getPhone()));
        employee.setDateOfBirth(form.getDateOfBirth());
        employee.setGender(normalizeNullable(form.getGender()));
        employee.setAddress(normalizeNullable(form.getAddress()));
        employee.setHireDate(form.getHireDate());
        employee.setTerminationDate(form.getTerminationDate());
        employee.setStatus(form.getStatus());
        employee.setDepartment(resolveDepartment(form.getDepartmentId()));
        employee.setPosition(resolvePosition(form.getPositionId()));
        employee.setManager(resolveManager(form.getManagerId(), id));
        return employeeRepository.save(employee);
    }

    private void ensureUnique(String employeeCode, String email, Long currentId) {
        boolean duplicateCode = currentId == null
                ? employeeRepository.existsByEmployeeCodeIgnoreCase(employeeCode)
                : employeeRepository.existsByEmployeeCodeIgnoreCaseAndIdNot(employeeCode, currentId);
        if (duplicateCode) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + employeeCode);
        }

        boolean duplicateEmail = currentId == null
                ? employeeRepository.existsByEmailIgnoreCase(email)
                : employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (duplicateEmail) {
            throw new IllegalArgumentException("Email nhân viên đã tồn tại: " + email);
        }
    }

    private void validateDates(EmployeeForm form) {
        if (form.getTerminationDate() != null && form.getTerminationDate().isBefore(form.getHireDate())) {
            throw new IllegalArgumentException("Ngày nghỉ việc không thể trước ngày vào làm");
        }
    }

    private Department resolveDepartment(Long id) {
        return id == null ? null : departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban #" + id));
    }

    private Position resolvePosition(Long id) {
        return id == null ? null : positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí #" + id));
    }

    private Employee resolveManager(Long id, Long currentId) {
        if (id == null) {
            return null;
        }
        if (id.equals(currentId)) {
            throw new IllegalArgumentException("Nhân viên không thể là quản lý của chính mình");
        }
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản lý #" + id));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        String normalized = normalize(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }
}
