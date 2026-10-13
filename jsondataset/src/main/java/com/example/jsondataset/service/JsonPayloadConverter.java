package com.example.jsondataset.service;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** Converts between a JSON record (Map) and the text stored in the database. */
@Component
public class JsonPayloadConverter {

    private static final TypeReference<LinkedHashMap<String, Object>> MAP_TYPE = new TypeReference<>() { };

    private final JsonMapper jsonMapper;

    public JsonPayloadConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public String toJson(Map<String, Object> record) {
        return jsonMapper.writeValueAsString(record);
    }

    public Map<String, Object> toMap(String json) {
        return jsonMapper.readValue(json, MAP_TYPE);
    }
}