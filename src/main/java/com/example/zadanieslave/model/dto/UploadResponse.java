package com.example.zadanieslave.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UploadResponse {
    private UUID documentId;
    private UUID versionId;
    private String status; // PENDING_VALIDATION, VALID, INVALID
}