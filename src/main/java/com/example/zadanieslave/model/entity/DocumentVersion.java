package com.example.zadanieslave.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "document_versions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private Integer versionNumber;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String xmlContent; // оригинальный XML (или хранить как путь к файлу)


    @Column(columnDefinition = "TEXT")
    private String parsedDataJson;

    @Column(nullable = false, length = 20)
    private String validationStatus; // PENDING_VALIDATION, VALID, INVALID

    @Column(columnDefinition = "TEXT")
    private String validationErrors; // ошибки валидации (если есть)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
    public void setParsedData(Map<String, Object> data) {
        try {
            this.parsedDataJson = new ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            this.parsedDataJson = "{}"; // fallback при ошибке
        }
    }

    /**
     * Возвращает данные, десериализуя JSON-строку в Map.
     */
    public Map<String, Object> getParsedData() {
        if (parsedDataJson == null || parsedDataJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return new ObjectMapper().readValue(parsedDataJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>(); // fallback при ошибке
        }
    }
}
