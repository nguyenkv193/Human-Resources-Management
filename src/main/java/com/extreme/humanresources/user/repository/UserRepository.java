package com.extreme.humanresources.user.repository;

import com.extreme.humanresources.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    List<User> findAllByOrderByUsernameAsc();

    @EntityGraph(attributePaths = "roles")
    List<User> findByUsernameContainingIgnoreCaseOrderByUsernameAsc(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    @EntityGraph(attributePaths = "roles")
    List<User> findTop5ByOrderByCreatedAtDesc();

    long countByEnabledTrue();

    long countByEnabledFalse();

    @Query("select count(distinct u) from User u join u.roles role where upper(role.name) = upper(:roleName)")
    long countByRoleNameIgnoreCase(@Param("roleName") String roleName);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
}
