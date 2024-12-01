package com.biddingSystem.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;

public class BidDTODeserializer implements Deserializer<BidDTO> {

    private final ObjectMapper objectMapper;

    public BidDTODeserializer() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
    }

    @Override
    public BidDTO deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }
            System.out.println("Deserializer - "+data);
            return objectMapper.readValue(data, BidDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize BidDTO", e);
        }
    }
}

