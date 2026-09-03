package com.extreme.humanresources.user.service;

import com.extreme.humanresources.user.dto.request.ChangePasswordRequest;
import com.extreme.humanresources.user.dto.request.CreateUserRequest;
import com.extreme.humanresources.user.dto.request.UpdateUserRequest;
import com.extreme.humanresources.user.dto.response.RoleResponse;
import com.extreme.humanresources.user.dto.response.UserResponse;
import com.extreme.humanresources.user.entity.Role;
import com.extreme.humanresources.user.entity.User;
import com.extreme.humanresources.user.exception.DuplicateUsernameException;
import com.extreme.humanresources.user.exception.InvalidCurrentPasswordException;
import com.extreme.humanresources.user.exception.RoleNotFoundException;
import com.extreme.humanresources.user.exception.UserNotFoundException;
import com.extreme.humanresources.user.repository.RoleRepository;
import com.extreme.humanresources.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        List<User> users = normalizedKeyword.isBlank()
                ? userRepository.findAllByOrderByUsernameAsc()
                : userRepository.findByUsernameContainingIgnoreCaseOrderByUsernameAsc(normalizedKeyword);

        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAllByOrderByNameAsc().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String username = normalizeUsername(request.getUsername());
        ensureUsernameAvailable(username, null);

        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .employeeId(request.getEmployeeId())
                .roles(resolveRoles(request.getRoleIds()))
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = getUser(id);
        String username = normalizeUsername(request.getUsername());
        ensureUsernameAvailable(username, id);

        user.setUsername(username);
        user.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        user.setEmployeeId(request.getEmployeeId());
        user.setRoles(resolveRoles(request.getRoleIds()));

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = getUser(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse toggleEnabled(Long id) {
        User user = getUser(id);
        user.setEnabled(!user.isEnabled());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void ensureUsernameAvailable(String username, Long currentUserId) {
        boolean exists = currentUserId == null
                ? userRepository.existsByUsernameIgnoreCase(username)
                : userRepository.existsByUsernameIgnoreCaseAndIdNot(username, currentUserId);

        if (exists) {
            throw new DuplicateUsernameException(username);
        }
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        Set<Long> requestedIds = roleIds == null
                ? new HashSet<>()
                : roleIds.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        if (requestedIds.isEmpty()) {
            throw new RoleNotFoundException("User phải có ít nhất một role");
        }

        List<Role> roles = roleRepository.findAllById(requestedIds);
        Set<Long> foundIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
        requestedIds.removeAll(foundIds);

        if (!requestedIds.isEmpty()) {
            throw new RoleNotFoundException("Không tìm thấy role: " + requestedIds);
        }

        return new HashSet<>(roles);
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private UserResponse toResponse(User user) {
        Set<RoleResponse> roles = user.getRoles().stream()
                .sorted(Comparator.comparing(Role::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toRoleResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .employeeId(user.getEmployeeId())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
