package com.example.zadanieslave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public String store(MultipartFile file) throws IOException {
        // Приводим к абсолютному нормализованному пути
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Безопасно получаем расширение
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Генерируем уникальное имя файла
        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(fileName).normalize().toAbsolutePath();

        // Проверка, что итоговый путь внутри uploadPath
        if (!filePath.startsWith(uploadPath)) {
            throw new IOException("Недопустимый путь сохранения: " + filePath);
        }

        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }
}