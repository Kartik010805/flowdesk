package com.example.demo.controller;

import com.example.demo.service.AdminRequestService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final AdminRequestService adminRequestService;

    public AdminRequestController(
            AdminRequestService adminRequestService) {
        this.adminRequestService = adminRequestService;
    }

    @GetMapping
    public String viewAllRequests(Model model) {

        model.addAttribute(
                "requests",
                adminRequestService.getAllRequests()
        );

        return "admin-requests";
    }
}