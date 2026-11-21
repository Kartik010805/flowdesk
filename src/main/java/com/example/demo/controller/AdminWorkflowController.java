
package com.example.demo.controller;

import com.example.demo.entity.ApprovalStep;
import com.example.demo.repository.ApprovalStepRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/workflows")
public class AdminWorkflowController {

    private final ApprovalStepRepository approvalStepRepository;

    public AdminWorkflowController(
            ApprovalStepRepository approvalStepRepository) {
        this.approvalStepRepository = approvalStepRepository;
    }

    @GetMapping
    public String viewWorkflows(Model model) {

        model.addAttribute(
                "approvalSteps",
                approvalStepRepository.findAll()
        );

        return "admin-workflows";
    }
}
