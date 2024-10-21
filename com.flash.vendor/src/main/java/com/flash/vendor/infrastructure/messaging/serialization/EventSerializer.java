package com.flash.vendor.infrastructure.messaging.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(EventSerializer.class);
    private final ObjectMapper kafkaObjectMapper;

    public EventSerializer() {
        this.kafkaObjectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }


    // 직렬화 (객체 -> JSON 문자열)
    public <T> String serialize(T object) {
        try {
            return kafkaObjectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object: {}", object, e);
            throw new RuntimeException("Serialization error", e);
        }
    }

    // 역직렬화 (JSON 문자열 -> 객체)
    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return kafkaObjectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON: {}", json, e);
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
