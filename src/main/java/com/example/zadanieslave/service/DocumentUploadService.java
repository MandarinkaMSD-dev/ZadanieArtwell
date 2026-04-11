package com.example.zadanieslave.service;

import com.example.zadanieslave.model.dto.UploadResponse;
import com.example.zadanieslave.model.entity.*;
import com.example.zadanieslave.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.zadanieslave.audit.Auditable;
import com.example.zadanieslave.repository.AuditLogRepository;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentUploadService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final UserRepository userRepository;
    private final ConstructionProjectRepository projectRepository;
    private final XmlProcessingService xmlProcessingService;
    private final FileStorageService fileStorageService; // реализуем далее
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;
    @Auditable(action = "UPLOAD_DOCUMENT", entityType = "Document")
    @Transactional
    public UploadResponse uploadDocument(MultipartFile file, UUID projectId, UUID userId) throws IOException {
        // 1. Получаем сущности
        ConstructionProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Проект не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // 2. Сохраняем файл в хранилище
        String filePath = fileStorageService.store(file);

        // 3. Создаём запись документа
        Document document = Document.builder()
                .project(project)
                .uploadedBy(user)
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .currentVersion(1)
                .build();

        document = documentRepository.save(document);

        // 4. Создаём первую версию документа
        DocumentVersion version = DocumentVersion.builder()
                .document(document)
                .versionNumber(1)
                .xmlContent(new String(file.getBytes())) // или сохраняем только путь
                .validationStatus("PENDING_VALIDATION")
                .build();

        version = versionRepository.save(version);

        // 5. Запускаем асинхронную валидацию и парсинг
        xmlProcessingService.validateAndParse(version, file.getBytes());

        AuditLog auditLog = AuditLog.builder()
                .action("UPLOAD_DOCUMENT")
                .entityType("Document")
                .entityId(document.getId())
                .user(user)
                .details("Загружен файл: " + file.getOriginalFilename())
                .build();
        auditLogRepository.save(auditLog);
// Аудит (выполняется в отдельной транзакции, не влияет на основную)
        try {
            auditLogService.log(
                    "UPLOAD_DOCUMENT",
                    "Document",
                    document.getId(),
                    user,
                    "Загружен файл: " + file.getOriginalFilename()
            );
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем основной поток
            log.error("Не удалось сохранить аудит", e);
        }
        // 6. Возвращаем ответ
        return new UploadResponse(document.getId(), version.getId(), "PENDING_VALIDATION");
    }
    public UUID testAudit() {
        AuditLog log = AuditLog.builder()
                .action("TEST_MANUAL")
                .entityType("Test")
                .entityId(UUID.randomUUID())
                .user(null)
                .details("Ручной тест аудита")
                .build();
        auditLogRepository.save(log);
        return log.getId();
    }
}