
package com.example.demo.service;

import com.example.demo.entity.Approval;
import com.example.demo.entity.ApprovalStep;
import com.example.demo.entity.Request;
import com.example.demo.entity.User;
import com.example.demo.enums.ApprovalStatus;
import com.example.demo.enums.RequestStatus;
import com.example.demo.enums.Role;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.ApprovalStepRepository;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ApprovalService(
            ApprovalRepository approvalRepository,
            ApprovalStepRepository approvalStepRepository,
            RequestRepository requestRepository,
            UserRepository userRepository,
            AuditLogService auditLogService) {

        this.approvalRepository = approvalRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void processDecision(
            Long requestId,
            String managerEmail,
            boolean approved,
            String comments) {

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new RuntimeException(
                    "Only managers can make approval decisions");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Request not found"));

        if (request.getEmployee().getManager() == null ||
                !request.getEmployee().getManager().getId()
                        .equals(manager.getId())) {

            throw new RuntimeException(
                    "You are not authorized to decide this request");
        }

        if (request.getStatus() != RequestStatus.SUBMITTED &&
                request.getStatus() != RequestStatus.IN_REVIEW) {

            throw new RuntimeException(
                    "This request has already been processed");
        }

        List<ApprovalStep> steps =
                approvalStepRepository
                        .findByRequestTypeAndActiveTrueOrderByStepOrderAsc(
                                request.getType());

        if (steps.isEmpty()) {
            throw new RuntimeException(
                    "No active approval step configured for this request");
        }

        ApprovalStep step = steps.get(0);

        if (!step.getApproverRole().equals(manager.getRole().name())) {
            throw new RuntimeException(
                    "You are not authorized for this approval step");
        }

        Approval approval = new Approval();
        approval.setRequest(request);
        approval.setApprovalStep(step);
        approval.setApprover(manager);
        approval.setStatus(
                approved
                        ? ApprovalStatus.APPROVED
                        : ApprovalStatus.REJECTED
        );
        approval.setComments(comments);
        approval.setActionDate(LocalDateTime.now());

        approvalRepository.save(approval);

        request.setStatus(
                approved
                        ? RequestStatus.APPROVED
                        : RequestStatus.REJECTED
        );

        requestRepository.save(request);

        auditLogService.log(
                managerEmail,
                approved ? "REQUEST_APPROVED" : "REQUEST_REJECTED",
                "Request #" + request.getId()
                        + " (" + request.getTitle() + ") was "
                        + (approved ? "approved" : "rejected")
                        + ". Comments: "
                        + (comments == null || comments.isBlank()
                        ? "None"
                        : comments)
        );
    }
}
