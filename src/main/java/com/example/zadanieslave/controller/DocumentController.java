package com.example.zadanieslave.controller;

import com.example.zadanieslave.model.dto.UploadResponse;
import com.example.zadanieslave.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j   // <-- ВОТ ЭТО ДОБАВЬ
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
        } catch (Exception e) {
            log.error("Ошибка при загрузке документа", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}