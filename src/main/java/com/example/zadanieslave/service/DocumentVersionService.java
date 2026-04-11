package com.example.zadanieslave.service;

import com.example.zadanieslave.model.entity.DocumentVersion;
import com.example.zadanieslave.repository.DocumentVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentVersionService {

    private final DocumentVersionRepository versionRepository;

    @Transactional
    public DocumentVersion save(DocumentVersion version) {
        return versionRepository.save(version);
    }
}