package com.example.zadanieslave.service;

import com.example.zadanieslave.model.entity.AuditLog;
import com.example.zadanieslave.model.entity.User;
import com.example.zadanieslave.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, UUID entityId, User user, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .user(user)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}