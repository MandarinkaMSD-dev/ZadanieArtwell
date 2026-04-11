package com.example.zadanieslave.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private UUID id;
    private String fileName;
    private Integer currentVersion;
    private String validationStatus; // статус последней версии
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID projectId;
    private String projectName;
    private UUID uploadedById;
    private String uploadedByUsername;
}