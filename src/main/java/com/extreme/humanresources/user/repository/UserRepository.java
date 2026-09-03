package com.extreme.humanresources.user.repository;

import com.extreme.humanresources.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    List<User> findAllByOrderByUsernameAsc();

    @EntityGraph(attributePaths = "roles")
    List<User> findByUsernameContainingIgnoreCaseOrderByUsernameAsc(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
}
