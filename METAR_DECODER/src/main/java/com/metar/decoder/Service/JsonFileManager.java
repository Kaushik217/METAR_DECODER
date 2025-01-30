package com.metar.decoder.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileManager {
	
	private final ObjectMapper objectMapper;

	public JsonFileManager(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}
	
	//Read JSON data from a file
	public String readJsonDataFromFile(String filePath) throws IOException{
		if(Files.exists(Paths.get(filePath))) {
			return new String(Files.readAllBytes(Paths.get(filePath)));
		}
		else {
			throw new IOException("File not found: " + filePath);
		}
	}
	
	//Write JSON data to a file
	public void WriteJsonDataToFile(String filePath, String jsonData) throws IOException{
		Files.write(Paths.get(filePath), jsonData.getBytes());
	}
	
	//Deserialize JSON string to a list
	public List<String> deserializeJsonToList(String jsonData) throws IOException{
		return getObjectMapper().readValue(jsonData, new TypeReference<List<String>>() {});
	}
	
	//clear the output file before every build
	public void clearFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.writeString(path, ""); // Clear the file by writing an empty string
        }
    }

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
