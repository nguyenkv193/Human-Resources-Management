package com.extreme.humanresources.attendance.controller;

import com.extreme.humanresources.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public String index(Authentication authentication, Model model) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        var currentEmployee = attendanceService.findCurrentEmployee(authentication.getName());

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentEmployee", currentEmployee.orElse(null));
        model.addAttribute("todayRecord", attendanceService.findToday(authentication.getName()).orElse(null));
        model.addAttribute("attendanceRecords", isAdmin
                ? attendanceService.findAll()
                : currentEmployee.map(employee -> attendanceService.findByEmployee(employee.getId())).orElseGet(java.util.List::of));
        model.addAttribute("activePage", "attendance");
        return "attendance/index";
    }

    @PostMapping("/check-in")
    public String checkIn(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.checkIn(authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Check-in thành công");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/attendance";
    }

    @PostMapping("/check-out")
    public String checkOut(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.checkOut(authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Check-out thành công");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/attendance";
    }
}
