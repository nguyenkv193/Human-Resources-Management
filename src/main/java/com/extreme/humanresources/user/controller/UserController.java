package com.extreme.humanresources.user.controller;

import com.extreme.humanresources.user.dto.request.ChangePasswordRequest;
import com.extreme.humanresources.user.dto.request.CreateUserRequest;
import com.extreme.humanresources.user.dto.request.UpdateUserRequest;
import com.extreme.humanresources.user.dto.response.UserResponse;
import com.extreme.humanresources.user.exception.DuplicateUsernameException;
import com.extreme.humanresources.user.exception.InvalidCurrentPasswordException;
import com.extreme.humanresources.user.exception.RoleNotFoundException;
import com.extreme.humanresources.user.exception.UserNotFoundException;
import com.extreme.humanresources.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("users", userService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        return "user/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new CreateUserRequest());
        prepareForm(model, false, null);
        return "user/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("userForm") CreateUserRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, false, null);
            return "user/form";
        }

        try {
            userService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo user thành công");
            return "redirect:/users";
        } catch (DuplicateUsernameException | RoleNotFoundException exception) {
            bindingResult.reject("user.error", exception.getMessage());
            prepareForm(model, false, null);
            return "user/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserResponse user = userService.findById(id);
        UpdateUserRequest form = new UpdateUserRequest();
        form.setUsername(user.getUsername());
        form.setEnabled(user.isEnabled());
        form.setEmployeeId(user.getEmployeeId());
        form.setRoleIds(user.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toCollection(HashSet::new)));

        model.addAttribute("userForm", form);
        prepareForm(model, true, id);
        return "user/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("userForm") UpdateUserRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, true, id);
            return "user/form";
        }

        try {
            userService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật user thành công");
            return "redirect:/users";
        } catch (DuplicateUsernameException | RoleNotFoundException exception) {
            bindingResult.reject("user.error", exception.getMessage());
            prepareForm(model, true, id);
            return "user/form";
        }
    }

    @GetMapping("/{id}/password")
    public String changePasswordForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("passwordForm", new ChangePasswordRequest());
        return "user/change-password";
    }

    @PostMapping("/{id}/password")
    public String changePassword(
            @PathVariable Long id,
            @Valid @ModelAttribute("passwordForm") ChangePasswordRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.findById(id));
            return "user/change-password";
        }

        try {
            userService.changePassword(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công");
            return "redirect:/users";
        } catch (InvalidCurrentPasswordException exception) {
            bindingResult.reject("password.error", exception.getMessage());
            model.addAttribute("user", userService.findById(id));
            return "user/change-password";
        }
    }

    @PostMapping("/{id}/toggle")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UserResponse user = userService.toggleEnabled(id);
        String message = user.isEnabled() ? "Đã kích hoạt user" : "Đã vô hiệu hóa user";
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa user thành công");
        return "redirect:/users";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleNotFound(UserNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error/404";
    }

    private void prepareForm(Model model, boolean isEdit, Long userId) {
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("userId", userId);
    }
}
