
package com.example.demo.controller;

import com.example.demo.enums.RequestType;
import com.example.demo.service.RequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/new")
    public String showRequestForm(Model model) {
        model.addAttribute("requestTypes", RequestType.values());
        return "employee-request-form";
    }

    @PostMapping("/create")
    public String createRequest(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam RequestType type,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        requestService.createRequest(
                authentication.getName(),
                title,
                description,
                type
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your request has been submitted successfully."
        );

        return "redirect:/employee/requests/new";
    }

    @GetMapping
    public String myRequests(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "requests",
                requestService.getMyRequests(authentication.getName())
        );

        return "employee-my-requests";
    }

    @GetMapping("/{requestId}")
    public String requestDetails(
            @PathVariable Long requestId,
            Authentication authentication,
            Model model) {

        RequestService.RequestDetails details =
                requestService.getRequestDetails(
                        requestId,
                        authentication.getName()
                );

        model.addAttribute("request", details.request());
        model.addAttribute("approvals", details.approvals());

        return "employee-request-details";
    }
}
