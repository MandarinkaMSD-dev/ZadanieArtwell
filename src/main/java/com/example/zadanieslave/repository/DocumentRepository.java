package com.example.zadanieslave.repository;

import com.example.zadanieslave.model.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Page<Document> findByProjectId(UUID projectId, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d " +
            "JOIN FETCH d.project " +
            "JOIN FETCH d.uploadedBy " +
            "LEFT JOIN FETCH d.versions " +
            "WHERE d.project.id = :projectId")
    List<Document> findByProjectIdWithRelations(@Param("projectId") UUID projectId);
    @Query("SELECT d FROM Document d " +
            "JOIN FETCH d.project " +
            "JOIN FETCH d.uploadedBy " +
            "LEFT JOIN FETCH d.versions " +
            "WHERE d.id = :id")
    Optional<Document> findByIdWithVersions(@Param("id") UUID id);
}