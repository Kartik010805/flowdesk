package com.example.demo.repository;

import com.example.demo.entity.ApprovalStep;
import com.example.demo.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByRequestTypeAndActiveTrueOrderByStepOrderAsc(RequestType requestType);
}