
package com.example.demo.controller;

import com.example.demo.service.DashboardService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return "redirect:/manager/dashboard";
        }

        return "redirect:/employee/dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "stats",
                dashboardService.getEmployeeStats(authentication.getName())
        );

        return "employee-dashboard";
    }

    @GetMapping("/manager/dashboard")
    public String managerDashboard(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "stats",
                dashboardService.getManagerStats(authentication.getName())
        );

        return "manager-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "stats",
                dashboardService.getAdminStats()
        );

        return "admin-dashboard";
    }
}
