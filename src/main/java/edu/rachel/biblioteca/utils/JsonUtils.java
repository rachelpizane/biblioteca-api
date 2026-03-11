package edu.rachel.biblioteca.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter para JSON", e);
        }
    }

    private JsonUtils(){}
}
