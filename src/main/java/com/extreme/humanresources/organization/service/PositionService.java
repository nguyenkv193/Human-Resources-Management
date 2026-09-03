package com.extreme.humanresources.organization.service;

import com.extreme.humanresources.common.exception.ResourceNotFoundException;
import com.extreme.humanresources.organization.dto.request.PositionForm;
import com.extreme.humanresources.organization.entity.Position;
import com.extreme.humanresources.organization.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;

    public List<Position> findAll() {
        return positionRepository.findAllByOrderByNameAsc();
    }

    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vị trí #" + id));
    }

    public PositionForm getForm(Long id) {
        Position position = findById(id);
        PositionForm form = new PositionForm();
        form.setCode(position.getCode());
        form.setName(position.getName());
        form.setDescription(position.getDescription());
        form.setStatus(position.getStatus());
        return form;
    }

    @Transactional
    public Position create(PositionForm form) {
        String code = normalize(form.getCode());
        ensureCodeAvailable(code, null);

        Position position = Position.builder()
                .code(code)
                .name(normalize(form.getName()))
                .description(normalizeNullable(form.getDescription()))
                .status(form.getStatus())
                .build();
        return positionRepository.save(position);
    }

    @Transactional
    public Position update(Long id, PositionForm form) {
        Position position = findById(id);
        String code = normalize(form.getCode());
        ensureCodeAvailable(code, id);

        position.setCode(code);
        position.setName(normalize(form.getName()));
        position.setDescription(normalizeNullable(form.getDescription()));
        position.setStatus(form.getStatus());
        return positionRepository.save(position);
    }

    private void ensureCodeAvailable(String code, Long currentId) {
        boolean exists = currentId == null
                ? positionRepository.existsByCodeIgnoreCase(code)
                : positionRepository.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        if (exists) {
            throw new IllegalArgumentException("Mã vị trí đã tồn tại: " + code);
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
