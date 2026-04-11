package com.example.zadanieslave.repository;

import com.example.zadanieslave.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}