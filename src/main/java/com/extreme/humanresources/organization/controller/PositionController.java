package com.extreme.humanresources.organization.controller;

import com.extreme.humanresources.organization.dto.request.PositionForm;
import com.extreme.humanresources.organization.entity.PositionStatus;
import com.extreme.humanresources.organization.service.PositionService;
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
@RequestMapping("/positions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("positions", positionService.findAll());
        model.addAttribute("activePage", "positions");
        return "position/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("positionForm", new PositionForm());
        prepareForm(model, false, null);
        return "position/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("positionForm") PositionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, false, null);
            return "position/form";
        }
        try {
            positionService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo vị trí thành công");
            return "redirect:/positions";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("position.error", exception.getMessage());
            prepareForm(model, false, null);
            return "position/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("positionForm", positionService.getForm(id));
        prepareForm(model, true, id);
        return "position/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("positionForm") PositionForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, true, id);
            return "position/form";
        }
        try {
            positionService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật vị trí thành công");
            return "redirect:/positions";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("position.error", exception.getMessage());
            prepareForm(model, true, id);
            return "position/form";
        }
    }

    private void prepareForm(Model model, boolean edit, Long id) {
        model.addAttribute("positionStatuses", PositionStatus.values());
        model.addAttribute("isEdit", edit);
        model.addAttribute("positionId", id);
        model.addAttribute("activePage", "positions");
    }
}
