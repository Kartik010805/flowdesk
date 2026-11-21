
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.enums.EmploymentStatus;
import com.example.demo.enums.Role;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserController(
            UserService userService,
            UserRepository userRepository,
            DepartmentRepository departmentRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public String listUsers(Model model) {

        model.addAttribute(
                "users",
                userRepository.findAllByOrderByIdAsc()
        );

        model.addAttribute(
                "departments",
                departmentRepository.findAll()
        );

        model.addAttribute(
                "roles",
                List.of(Role.EMPLOYEE, Role.MANAGER)
        );

        List<User> activeManagers =
                userRepository.findByRoleAndStatusOrderByFullNameAsc(
                        Role.MANAGER,
                        EmploymentStatus.ACTIVE
                );

        model.addAttribute("managers", activeManagers);

        return "admin-users";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Role role,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long managerId,
            RedirectAttributes redirectAttributes) {

        try {

            userService.createUser(
                    fullName,
                    email,
                    password,
                    role,
                    departmentId,
                    managerId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account created successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/update/{userId}")
    public String updateUser(
            @PathVariable Long userId,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long managerId,
            RedirectAttributes redirectAttributes) {

        try {

            userService.updateUser(
                    userId,
                    fullName,
                    email,
                    departmentId,
                    managerId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User details updated successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/deactivate/{userId}")
    public String deactivateUser(
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes) {

        try {

            userService.deactivateUser(userId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account deactivated successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/activate/{userId}")
    public String activateUser(
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes) {

        try {

            userService.activateUser(userId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User account activated successfully."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/users";
    }
}
