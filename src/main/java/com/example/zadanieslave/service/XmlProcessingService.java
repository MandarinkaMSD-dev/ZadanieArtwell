package com.example.zadanieslave.service;

import com.example.zadanieslave.model.entity.DocumentVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class XmlProcessingService {

    @Value("${app.xml.xsd-path}")
    private Resource xsdResource;

    private final DocumentVersionService versionService;

    @Async
    public void validateAndParse(DocumentVersion version, byte[] xmlContent) {
        log.info("Начало обработки версии документа {}", version.getId());

        // 1. Валидация по XSD
        String validationErrors = validateXml(xmlContent);
        boolean isValid = validationErrors == null;

        // 2. Парсинг ключевых данных
        Map<String, Object> parsedData = isValid ? parseXml(xmlContent) : new HashMap<>();

        // 3. Обновление версии
        version.setValidationStatus(isValid ? "VALID" : "INVALID");
        version.setValidationErrors(validationErrors);
        version.setParsedData(parsedData);
        versionService.save(version);

        log.info("Обработка версии {} завершена. Статус: {}", version.getId(), version.getValidationStatus());
    }

    private String validateXml(byte[] xmlContent) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdResource.getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent)));
            return null; // успешно
        } catch (SAXException e) {
            return "Ошибка валидации XML: " + e.getMessage();
        } catch (IOException e) {
            return "Ошибка чтения схемы или XML: " + e.getMessage();
        }
    }

    private Map<String, Object> parseXml(byte[] xmlContent) {
        Map<String, Object> data = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent));

            // Пример извлечения данных (подставьте свои пути XPath)
            data.put("documentNumber", getTagValue(doc, "DocumentNumber"));
            data.put("documentDate", getTagValue(doc, "DocumentDate"));
            data.put("contractor", getTagValue(doc, "Contractor"));
            // Добавьте другие поля по вашей схеме

        } catch (Exception e) {
            log.error("Ошибка парсинга XML", e);
        }
        return data;
    }

    private String getTagValue(Document doc, String tagName) {
        var nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}