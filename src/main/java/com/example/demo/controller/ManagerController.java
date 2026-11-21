
package com.example.demo.controller;

import com.example.demo.service.ApprovalService;
import com.example.demo.service.RequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final RequestService requestService;
    private final ApprovalService approvalService;

    public ManagerController(
            RequestService requestService,
            ApprovalService approvalService) {

        this.requestService = requestService;
        this.approvalService = approvalService;
    }

    @GetMapping("/requests")
    public String viewRequests(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "requests",
                requestService.getManagerRequests(authentication.getName())
        );

        return "manager-requests";
    }

    @PostMapping("/requests/{requestId}/decision")
    public String processDecision(
            @PathVariable Long requestId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            approvalService.processDecision(
                    requestId,
                    authentication.getName(),
                    approved,
                    comments
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    approved
                            ? "Request approved successfully."
                            : "Request rejected successfully."
            );

        } catch (RuntimeException exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/manager/requests";
    }
}
