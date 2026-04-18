package com.example.zadanieslave.service;

import com.example.zadanieslave.model.dto.*;
import com.example.zadanieslave.model.entity.*;
import com.example.zadanieslave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentQueryService {

    private final DocumentRepository documentRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<DocumentDto> getDocumentsByProject(UUID projectId) {
        List<Document> documents = documentRepository.findByProjectIdWithRelations(projectId);
        return documents.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<DocumentDto> getDocumentsByProject(UUID projectId, Pageable pageable) {
        // Для пагинации оставляем базовый метод без JOIN FETCH versions
        return documentRepository.findByProjectId(projectId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public DocumentDetailDto getDocumentDetail(UUID documentId) {
        Document document = documentRepository.findByIdWithVersions(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Документ не найден: " + documentId));
        try {
            auditLogService.log(
                    "VIEW_DOCUMENT_DETAIL",
                    "Document",
                    document.getId(),
                    null, // пользователь не известен на этом этапе
                    "Просмотр деталей документа"
            );
        } catch (Exception e) {
            log.error("Ошибка сохранения аудита просмотра: {}", e.getMessage());
        }

        List<DocumentVersionDto> versionDtos = document.getVersions().stream()
                .map(this::toVersionDto)
                .collect(Collectors.toList());

        return DocumentDetailDto.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .currentVersion(document.getCurrentVersion())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .projectId(document.getProject().getId())
                .projectName(document.getProject().getName())
                .uploadedById(document.getUploadedBy().getId())
                .uploadedByUsername(document.getUploadedBy().getUsername())
                .versions(versionDtos)
                .build();
    }

    private DocumentDto toDto(Document doc) {
        String validationStatus = doc.getVersions().stream()
                .filter(v -> v.getVersionNumber().equals(doc.getCurrentVersion()))
                .findFirst()
                .map(DocumentVersion::getValidationStatus)
                .orElse("UNKNOWN");

        return DocumentDto.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .currentVersion(doc.getCurrentVersion())
                .validationStatus(validationStatus)
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .projectId(doc.getProject().getId())
                .projectName(doc.getProject().getName())
                .uploadedById(doc.getUploadedBy().getId())
                .uploadedByUsername(doc.getUploadedBy().getUsername())
                .build();
    }

    private DocumentVersionDto toVersionDto(DocumentVersion version) {
        return DocumentVersionDto.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .validationStatus(version.getValidationStatus())
                .validationErrors(version.getValidationErrors())
                .parsedData(version.getParsedData()) // вызовет десериализацию из JSON строки
                .uploadedAt(version.getUploadedAt())
                .build();
    }
}