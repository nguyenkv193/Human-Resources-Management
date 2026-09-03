package com.extreme.humanresources.organization.controller;

import com.extreme.humanresources.organization.dto.request.DepartmentForm;
import com.extreme.humanresources.organization.entity.DepartmentStatus;
import com.extreme.humanresources.organization.service.DepartmentService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("activePage", "departments");
        return "department/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("departmentForm", new DepartmentForm());
        prepareForm(model, false, null);
        return "department/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("departmentForm") DepartmentForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, false, null);
            return "department/form";
        }
        try {
            departmentService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo phòng ban thành công");
            return "redirect:/departments";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("department.error", exception.getMessage());
            prepareForm(model, false, null);
            return "department/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("departmentForm", departmentService.getForm(id));
        prepareForm(model, true, id);
        return "department/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("departmentForm") DepartmentForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, true, id);
            return "department/form";
        }
        try {
            departmentService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng ban thành công");
            return "redirect:/departments";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("department.error", exception.getMessage());
            prepareForm(model, true, id);
            return "department/form";
        }
    }

    private void prepareForm(Model model, boolean edit, Long id) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("departmentStatuses", DepartmentStatus.values());
        model.addAttribute("isEdit", edit);
        model.addAttribute("departmentId", id);
        model.addAttribute("activePage", "departments");
    }
}
