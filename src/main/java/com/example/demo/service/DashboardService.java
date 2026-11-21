
package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.enums.RequestStatus;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public DashboardService(
            RequestRepository requestRepository,
            UserRepository userRepository) {

        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public record DashboardStats(
            long totalUsers,
            long totalRequests,
            long pending,
            long approved,
            long rejected
    ) {}

    public DashboardStats getAdminStats() {

        long totalUsers = userRepository.count();
        long totalRequests = requestRepository.count();

        long pending =
                requestRepository.countByStatus(RequestStatus.SUBMITTED)
                        + requestRepository.countByStatus(RequestStatus.IN_REVIEW);

        long approved =
                requestRepository.countByStatus(RequestStatus.APPROVED);

        long rejected =
                requestRepository.countByStatus(RequestStatus.REJECTED);

        return new DashboardStats(
                totalUsers,
                totalRequests,
                pending,
                approved,
                rejected
        );
    }

    public DashboardStats getManagerStats(String managerEmail) {

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Manager not found"));

        Long managerId = manager.getId();

        long totalRequests =
                requestRepository.countByEmployee_Manager_Id(managerId);

        long pending =
                requestRepository.countByEmployee_Manager_IdAndStatusIn(
                        managerId,
                        List.of(
                                RequestStatus.SUBMITTED,
                                RequestStatus.IN_REVIEW
                        )
                );

        long approved =
                requestRepository.countByEmployee_Manager_IdAndStatus(
                        managerId,
                        RequestStatus.APPROVED
                );

        long rejected =
                requestRepository.countByEmployee_Manager_IdAndStatus(
                        managerId,
                        RequestStatus.REJECTED
                );

        return new DashboardStats(
                0,
                totalRequests,
                pending,
                approved,
                rejected
        );
    }

    public DashboardStats getEmployeeStats(String employeeEmail) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Long employeeId = employee.getId();

        long totalRequests =
                requestRepository.countByEmployee_Id(employeeId);

        long pending =
                requestRepository.countByEmployee_IdAndStatusIn(
                        employeeId,
                        List.of(
                                RequestStatus.SUBMITTED,
                                RequestStatus.IN_REVIEW
                        )
                );

        long approved =
                requestRepository.countByEmployee_IdAndStatus(
                        employeeId,
                        RequestStatus.APPROVED
                );

        long rejected =
                requestRepository.countByEmployee_IdAndStatus(
                        employeeId,
                        RequestStatus.REJECTED
                );

        return new DashboardStats(
                0,
                totalRequests,
                pending,
                approved,
                rejected
        );
    }
}
