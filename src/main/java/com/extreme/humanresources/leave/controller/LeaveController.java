package com.extreme.humanresources.leave.controller;

import com.extreme.humanresources.leave.dto.request.LeaveRequestForm;
import com.extreme.humanresources.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public String list(Authentication authentication, Model model) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        var currentEmployee = leaveService.findCurrentEmployeeOptional(authentication.getName());

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentEmployee", currentEmployee.orElse(null));
        model.addAttribute("leaveRequests", isAdmin
                ? leaveService.findAll()
                : currentEmployee.map(employee -> leaveService.findByEmployee(employee.getId())).orElseGet(java.util.List::of));
        model.addAttribute("activePage", "leave");
        return "leave/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("leaveForm", new LeaveRequestForm());
        model.addAttribute("leaveTypes", leaveService.findActiveTypes());
        model.addAttribute("activePage", "leave");
        return "leave/form";
    }

    @PostMapping
    public String create(Authentication authentication,
                         @Valid @ModelAttribute("leaveForm") LeaveRequestForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("leaveTypes", leaveService.findActiveTypes());
            model.addAttribute("activePage", "leave");
            return "leave/form";
        }
        try {
            leaveService.create(authentication.getName(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi đơn nghỉ phép thành công");
            return "redirect:/leave";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            bindingResult.reject("leave.error", exception.getMessage());
            model.addAttribute("leaveTypes", leaveService.findActiveTypes());
            model.addAttribute("activePage", "leave");
            return "leave/form";
        }
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approve(@PathVariable Long id,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        processApproval(() -> leaveService.approve(id, authentication.getName()), "Đã duyệt đơn nghỉ phép", redirectAttributes);
        return "redirect:/leave";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String reject(@PathVariable Long id,
                         @RequestParam(defaultValue = "Không được phê duyệt") String reason,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        processApproval(() -> leaveService.reject(id, authentication.getName(), reason), "Đã từ chối đơn nghỉ phép", redirectAttributes);
        return "redirect:/leave";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            leaveService.cancel(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn nghỉ phép");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/leave";
    }

    private void processApproval(java.util.function.Supplier<?> action,
                                 String successMessage,
                                 RedirectAttributes redirectAttributes) {
        try {
            action.get();
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
    }
}
