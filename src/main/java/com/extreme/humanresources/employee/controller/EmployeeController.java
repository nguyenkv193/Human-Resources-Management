package com.extreme.humanresources.employee.controller;

import com.extreme.humanresources.employee.dto.request.EmployeeForm;
import com.extreme.humanresources.employee.entity.EmployeeStatus;
import com.extreme.humanresources.employee.service.EmployeeService;
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

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String status,
                       Model model) {
        EmployeeStatus statusFilter = parseStatus(status);
        model.addAttribute("employees", employeeService.findAll(keyword, statusFilter));
        model.addAttribute("employeeStatuses", EmployeeStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status == null ? "" : status);
        model.addAttribute("activePage", "employees");
        return "employee/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.findById(id));
        model.addAttribute("activePage", "employees");
        return "employee/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        prepareForm(model, false, null);
        return "employee/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("employeeForm") EmployeeForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, false, null);
            return "employee/form";
        }
        try {
            employeeService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo nhân viên thành công");
            return "redirect:/employees";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("employee.error", exception.getMessage());
            prepareForm(model, false, null);
            return "employee/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employeeForm", employeeService.getForm(id));
        prepareForm(model, true, id);
        return "employee/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employeeForm") EmployeeForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, true, id);
            return "employee/form";
        }
        try {
            employeeService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công");
            return "redirect:/employees/" + id;
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("employee.error", exception.getMessage());
            prepareForm(model, true, id);
            return "employee/form";
        }
    }

    private void prepareForm(Model model, boolean edit, Long id) {
        model.addAttribute("departments", employeeService.findDepartments());
        model.addAttribute("positions", employeeService.findPositions());
        model.addAttribute("managers", employeeService.findManagers(id));
        model.addAttribute("employeeStatuses", EmployeeStatus.values());
        model.addAttribute("isEdit", edit);
        model.addAttribute("employeeId", id);
        model.addAttribute("activePage", "employees");
    }

    private EmployeeStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return EmployeeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
