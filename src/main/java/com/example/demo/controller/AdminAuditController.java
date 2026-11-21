
package com.example.demo.controller;

import com.example.demo.repository.AuditLogRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/audit-logs")
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    public AdminAuditController(
            AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String viewAuditLogs(Model model) {

        model.addAttribute(
                "auditLogs",
                auditLogRepository.findAllByOrderByCreatedAtDesc()
        );

        return "admin-audit-logs";
    }
}
