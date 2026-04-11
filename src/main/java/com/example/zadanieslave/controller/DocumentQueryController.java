package com.example.zadanieslave.controller;

import com.example.zadanieslave.model.dto.DocumentDetailDto;
import com.example.zadanieslave.model.dto.DocumentDto;
import com.example.zadanieslave.service.DocumentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentQueryController {

    private final DocumentQueryService queryService;

    // GET /api/documents?projectId=<UUID> – список документов
    @GetMapping
    public ResponseEntity<List<DocumentDto>> getDocumentsByProject(
            @RequestParam UUID projectId) {
        List<DocumentDto> documents = queryService.getDocumentsByProject(projectId);
        return ResponseEntity.ok(documents);
    }

    // GET /api/documents/paged?projectId=<UUID>&page=0&size=10 – с пагинацией
    @GetMapping("/paged")
    public ResponseEntity<Page<DocumentDto>> getDocumentsPaged(
            @RequestParam UUID projectId,
            Pageable pageable) {
        Page<DocumentDto> page = queryService.getDocumentsByProject(projectId, pageable);
        return ResponseEntity.ok(page);
    }

    // GET /api/documents/{id} – детали документа с версиями
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailDto> getDocumentDetail(@PathVariable UUID id) {
        DocumentDetailDto detail = queryService.getDocumentDetail(id);
        return ResponseEntity.ok(detail);
    }
}