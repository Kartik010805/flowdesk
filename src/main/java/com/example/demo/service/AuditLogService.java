
package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void log(
            String actorEmail,
            String action,
            String details) {

        AuditLog auditLog = new AuditLog();

        auditLog.setActorEmail(actorEmail);
        auditLog.setAction(action);
        auditLog.setDetails(details);

        auditLogRepository.save(auditLog);
    }
}
