package com.example.zadanieslave.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailDto {
    private UUID id;
    private String fileName;
    private Integer currentVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID projectId;
    private String projectName;
    private UUID uploadedById;
    private String uploadedByUsername;
    private List<DocumentVersionDto> versions;
}