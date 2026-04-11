package com.example.zadanieslave.controller;

import com.example.zadanieslave.model.dto.UploadResponse;
import com.example.zadanieslave.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") UUID projectId,
            @RequestParam("userId") UUID userId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            UploadResponse response = uploadService.uploadDocument(file, projectId, userId);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/test-audit")
    public ResponseEntity<UUID> testAudit() {
        return ResponseEntity.ok(uploadService.testAudit());
    }
}