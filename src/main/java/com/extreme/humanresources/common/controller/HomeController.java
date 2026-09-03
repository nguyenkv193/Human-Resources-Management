package com.extreme.humanresources.common.controller;

import com.extreme.humanresources.dashboard.dto.response.DashboardSummary;
import com.extreme.humanresources.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/dashboard"})
    public String home(Model model) {
        DashboardSummary summary = dashboardService.getSummary();
        model.addAttribute("summary", summary);
        model.addAttribute("today", summary.getToday());
        model.addAttribute("activePage", "dashboard");
        return "dashboard/index";
    }
}
