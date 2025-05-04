package ru.hpclab.hl.module1.model;

import lombok.Data;

@Data
public class KafkaMessage {
    private String entity;
    private String operation;
    private Object payload;

    public KafkaMessage() {
    }

    public KafkaMessage(String entity, String operation, Object payload) {
        this.entity = entity;
        this.operation = operation;
        this.payload = payload;
    }
} 