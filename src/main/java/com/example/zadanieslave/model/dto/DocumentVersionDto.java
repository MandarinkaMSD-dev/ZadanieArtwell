package com.example.zadanieslave.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionDto {
    private UUID id;
    private Integer versionNumber;
    private String validationStatus;
    private String validationErrors;
    private Map<String, Object> parsedData;
    private LocalDateTime uploadedAt;
}