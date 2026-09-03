package com.extreme.humanresources.dashboard.service;

import com.extreme.humanresources.dashboard.dto.response.DashboardSummary;
import com.extreme.humanresources.user.entity.Role;
import com.extreme.humanresources.user.entity.User;
import com.extreme.humanresources.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TODAY_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));

    private final UserRepository userRepository;

    public DashboardSummary getSummary() {
        List<DashboardSummary.RecentUser> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toRecentUser)
                .toList();

        return DashboardSummary.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByEnabledTrue())
                .disabledUsers(userRepository.countByEnabledFalse())
                .adminUsers(userRepository.countByRoleNameIgnoreCase("ADMIN"))
                .standardUsers(userRepository.countByRoleNameIgnoreCase("USER"))
                .today(ZonedDateTime.now().format(TODAY_FORMATTER))
                .recentUsers(recentUsers)
                .build();
    }

    private DashboardSummary.RecentUser toRecentUser(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        String createdAt = user.getCreatedAt() == null
                ? "-"
                : user.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .format(DATE_FORMATTER);

        return DashboardSummary.RecentUser.builder()
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .roles(roles)
                .createdAt(createdAt)
                .build();
    }
}
