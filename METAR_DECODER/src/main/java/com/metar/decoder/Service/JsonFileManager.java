package com.metar.decoder.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileManager {

    private final ObjectMapper objectMapper;

    public JsonFileManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ✅ Read JSON from resources (works in Render)
    public String readJsonDataFromFile(String filePath) throws IOException {

        ClassPathResource resource = new ClassPathResource(filePath);

        if (!resource.exists()) {
            throw new IOException("File not found in classpath: " + filePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ✅ Write JSON to TEMP directory (Render safe)
    public void writeJsonDataToFile(String filePath, String jsonData) throws IOException {

        String tempDir = System.getProperty("java.io.tmpdir");
        File file = new File(tempDir + "/" + filePath);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonData);
        }
    }

    // Deserialize JSON string to a list
    public List<String> deserializeJsonToList(String jsonData) throws IOException {
        return objectMapper.readValue(jsonData, new TypeReference<List<String>>() {});
    }

    // Clear output file (temp dir)
    public void clearFile(String filePath) throws IOException {

        String tempDir = System.getProperty("java.io.tmpdir");
        File file = new File(tempDir + "/" + filePath);

        if (file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("");
            }
        }
    }
}
