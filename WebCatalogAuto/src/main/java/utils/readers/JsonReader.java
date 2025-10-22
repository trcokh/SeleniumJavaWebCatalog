package utils.readers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class JsonReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonReader() {}

    public static <T> T readJson(String filePath, Class<T> clazz) {
        try {
            return mapper.readValue(new File(filePath), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
}
