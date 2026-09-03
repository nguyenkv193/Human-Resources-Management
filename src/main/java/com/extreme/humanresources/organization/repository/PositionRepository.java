package com.extreme.humanresources.organization.repository;

import com.extreme.humanresources.organization.entity.Position;
import com.extreme.humanresources.organization.entity.PositionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findAllByOrderByNameAsc();

    List<Position> findAllByStatusOrderByNameAsc(PositionStatus status);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
