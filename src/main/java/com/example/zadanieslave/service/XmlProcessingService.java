package com.example.zadanieslave.service;

import com.example.zadanieslave.model.entity.DocumentVersion;
import com.example.zadanieslave.repository.DocumentVersionRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class XmlProcessingService {

    @Value("${app.xml.xsd-path}")
    private Resource xsdResource;

    private final DocumentVersionService versionService;
    private final DocumentVersionRepository versionRepository;

    @Async
    public void validateAndParse(UUID versionId, byte[] xmlContent) {
        DocumentVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Версия не найдена: " + versionId));

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
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent)));
            return null;
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
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent));

            data.put("documentNumber", getTagValue(doc, "DocumentNumber"));
            data.put("documentDate", getTagValue(doc, "DocumentDate"));
            data.put("contractor", getTagValue(doc, "Contractor"));
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