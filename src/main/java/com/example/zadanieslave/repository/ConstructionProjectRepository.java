package com.example.zadanieslave.repository;

import com.example.zadanieslave.model.entity.ConstructionProject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConstructionProjectRepository extends JpaRepository<ConstructionProject, UUID> {
}