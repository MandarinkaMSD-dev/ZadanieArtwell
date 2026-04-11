package com.example.zadanieslave.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // может быть null для системных действий

    @Column(nullable = false, length = 50)
    private String action; // UPLOAD_DOCUMENT, VALIDATE, VIEW, DOWNLOAD и т.д.

    @Column(length = 255)
    private String entityType; // тип сущности (Document, Project и т.д.)

    private UUID entityId; // идентификатор сущности

    @Column(columnDefinition = "TEXT")
    private String details; // дополнительные детали в JSON

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}