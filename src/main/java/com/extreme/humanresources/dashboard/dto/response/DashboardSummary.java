package com.extreme.humanresources.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardSummary {

    private final long totalUsers;
    private final long activeUsers;
    private final long disabledUsers;
    private final long adminUsers;
    private final long standardUsers;
    private final long totalEmployees;
    private final long activeEmployees;
    private final long pendingLeaveRequests;
    private final long attendanceToday;
    private final String today;
    private final List<RecentUser> recentUsers;

    @Getter
    @Builder
    public static class RecentUser {

        private final String username;
        private final boolean enabled;
        private final List<String> roles;
        private final String createdAt;
    }
}
