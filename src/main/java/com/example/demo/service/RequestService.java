
package com.example.demo.service;

import com.example.demo.entity.Approval;
import com.example.demo.entity.Request;
import com.example.demo.entity.User;
import com.example.demo.enums.RequestStatus;
import com.example.demo.enums.RequestType;
import com.example.demo.repository.ApprovalRepository;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ApprovalRepository approvalRepository;
    private final AuditLogService auditLogService;

    public RequestService(
            RequestRepository requestRepository,
            UserRepository userRepository,
            ApprovalRepository approvalRepository,
            AuditLogService auditLogService) {

        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
        this.auditLogService = auditLogService;
    }

    public record RequestDetails(
            Request request,
            List<Approval> approvals
    ) {}

    @Transactional
    public Request createRequest(
            String email,
            String title,
            String description,
            RequestType requestType) {

        User employee = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Request request = new Request();
        request.setTitle(title);
        request.setDescription(description);
        request.setType(requestType);
        request.setStatus(RequestStatus.SUBMITTED);
        request.setEmployee(employee);
        request.setDepartment(employee.getDepartment());

        Request savedRequest = requestRepository.save(request);

        auditLogService.log(
                email,
                "REQUEST_SUBMITTED",
                "Request #" + savedRequest.getId()
                        + " submitted: " + savedRequest.getTitle()
        );

        return savedRequest;
    }

    @Transactional(readOnly = true)
    public List<Request> getMyRequests(String email) {

        User employee = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        return requestRepository
                .findByEmployee_IdOrderByCreatedAtDesc(employee.getId());
    }

    @Transactional(readOnly = true)
    public List<Request> getManagerRequests(String managerEmail) {

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        return requestRepository
                .findByEmployee_Manager_IdAndStatusInOrderByCreatedAtDesc(
                        manager.getId(),
                        List.of(
                                RequestStatus.SUBMITTED,
                                RequestStatus.IN_REVIEW
                        )
                );
    }

    @Transactional(readOnly = true)
    public RequestDetails getRequestDetails(
            Long requestId,
            String employeeEmail) {

        Request request = requestRepository
                .findWithDetailsById(requestId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Request not found"
                        ));

        if (!request.getEmployee().getEmail()
                .equalsIgnoreCase(employeeEmail)) {

            throw new AccessDeniedException(
                    "You are not allowed to view this request"
            );
        }

        List<Approval> approvals =
                approvalRepository
                        .findByRequest_IdOrderByApprovalStep_StepOrderAsc(
                                requestId
                        );

        return new RequestDetails(request, approvals);
    }
}
