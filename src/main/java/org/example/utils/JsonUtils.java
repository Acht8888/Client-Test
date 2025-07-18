package org.example.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String payloadMapToJsonString(Map<String, String> data) {
        StringBuilder jsonBuilder = new StringBuilder();

        // Start the JSON structure
        jsonBuilder.append("{\"payload\":{");

        // Iterate through the map and construct key-value pairs
        boolean firstEntry = true; // Flag to handle commas between key-value pairs
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!firstEntry) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\"")
                    .append(entry.getKey())  // Key
                    .append("\":\"")
                    .append(entry.getValue())  // Value
                    .append("\"");
            firstEntry = false;
        }

        // End the JSON structure
        jsonBuilder.append("}}");

        return jsonBuilder.toString();
    }

    public String convertObjectToJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T convertJsonStringToObject(String jsonString, JavaType targetType) {
        try {
            return objectMapper.readValue(jsonString, targetType);
        } catch (Exception e) {
            return null;
        }
    }
}
