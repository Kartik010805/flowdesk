
package com.example.demo.repository;

import com.example.demo.entity.Approval;
import com.example.demo.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByApprover_IdAndStatusOrderByIdAsc(
            Long approverId,
            ApprovalStatus status
    );

    @EntityGraph(attributePaths = {"approver", "approvalStep"})
    List<Approval> findByRequest_IdOrderByApprovalStep_StepOrderAsc(
            Long requestId
    );
}
