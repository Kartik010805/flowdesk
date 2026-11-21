
package com.example.demo.repository;
import java.util.Optional;
import com.example.demo.entity.Request;
import com.example.demo.enums.RequestStatus;
import com.example.demo.enums.RequestStatus;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @EntityGraph(attributePaths = {"employee"})
    List<Request> findByEmployee_Manager_IdOrderByCreatedAtDesc(Long managerId);

    List<Request> findByEmployee_IdOrderByCreatedAtDesc(Long employeeId);

    List<Request> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    @EntityGraph(attributePaths = {"employee", "department"})
    List<Request> findAllByOrderByCreatedAtDesc();
    @EntityGraph(attributePaths = {"employee"})
    List<Request> findByEmployee_Manager_IdAndStatusInOrderByCreatedAtDesc(
            Long managerId,
            List<RequestStatus> statuses
    );
    @EntityGraph(attributePaths = {"employee", "department"})
    Optional<Request> findWithDetailsById(Long id);
    // Admin dashboard counts
    long countByStatus(RequestStatus status);

    // Manager dashboard counts
    long countByEmployee_Manager_Id(Long managerId);

    long countByEmployee_Manager_IdAndStatusIn(
            Long managerId,
            List<RequestStatus> statuses
    );

    long countByEmployee_Manager_IdAndStatus(
            Long managerId,
            RequestStatus status
    );

    // Employee dashboard counts
    long countByEmployee_Id(Long employeeId);

    long countByEmployee_IdAndStatusIn(
            Long employeeId,
            List<RequestStatus> statuses
    );

    long countByEmployee_IdAndStatus(
            Long employeeId,
            RequestStatus status
    );
}
