package org.example.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
