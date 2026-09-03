package com.extreme.humanresources.organization.service;

import com.extreme.humanresources.common.exception.ResourceNotFoundException;
import com.extreme.humanresources.organization.dto.request.DepartmentForm;
import com.extreme.humanresources.organization.entity.Department;
import com.extreme.humanresources.organization.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> findAll() {
        return departmentRepository.findAllByOrderByNameAsc();
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban #" + id));
    }

    public DepartmentForm getForm(Long id) {
        Department department = findById(id);
        DepartmentForm form = new DepartmentForm();
        form.setCode(department.getCode());
        form.setName(department.getName());
        form.setDescription(department.getDescription());
        form.setParentId(department.getParent() == null ? null : department.getParent().getId());
        form.setStatus(department.getStatus());
        return form;
    }

    @Transactional
    public Department create(DepartmentForm form) {
        String code = normalize(form.getCode());
        ensureCodeAvailable(code, null);

        Department department = Department.builder()
                .code(code)
                .name(normalize(form.getName()))
                .description(normalizeNullable(form.getDescription()))
                .parent(resolveParent(form.getParentId(), null))
                .status(form.getStatus())
                .build();

        return departmentRepository.save(department);
    }

    @Transactional
    public Department update(Long id, DepartmentForm form) {
        Department department = findById(id);
        String code = normalize(form.getCode());
        ensureCodeAvailable(code, id);

        department.setCode(code);
        department.setName(normalize(form.getName()));
        department.setDescription(normalizeNullable(form.getDescription()));
        department.setParent(resolveParent(form.getParentId(), id));
        department.setStatus(form.getStatus());
        return departmentRepository.save(department);
    }

    private Department resolveParent(Long parentId, Long currentId) {
        if (parentId == null) {
            return null;
        }
        if (parentId.equals(currentId)) {
            throw new IllegalArgumentException("Phòng ban không thể là phòng ban cha của chính nó");
        }
        return findById(parentId);
    }

    private void ensureCodeAvailable(String code, Long currentId) {
        boolean exists = currentId == null
                ? departmentRepository.existsByCodeIgnoreCase(code)
                : departmentRepository.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        if (exists) {
            throw new IllegalArgumentException("Mã phòng ban đã tồn tại: " + code);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        String normalized = normalize(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }
}
