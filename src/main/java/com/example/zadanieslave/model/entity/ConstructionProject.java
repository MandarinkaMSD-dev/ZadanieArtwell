package com.example.zadanieslave.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "construction_projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private String projectCode; // код проекта по Минстрою

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Связь с документами (один проект - много документов)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    // Вспомогательный метод для двусторонней связи
    public void addDocument(Document document) {
        documents.add(document);
        document.setProject(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setProject(null);
    }
}