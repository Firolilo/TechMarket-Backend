package com.techmarket.techmarket.specialists.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpecialistJsonListMapper {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public SpecialistJsonListMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(List<String> values) {
        if (values == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("List cannot be serialized", ex);
        }
    }

    public List<String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, STRING_LIST);
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }
}
