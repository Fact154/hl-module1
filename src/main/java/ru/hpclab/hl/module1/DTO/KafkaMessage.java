package ru.hpclab.hl.module1.DTO;

import lombok.Data;

@Data
public class KafkaMessage {
    private String entity;        // Тип объекта (USER, DOCUMENT, TRANSACTION и т.д.)
    
    private String operation;     // Тип операции (POST, PUT, DEL, CLEAR)
    
    private Object payload;       // Данные для обработки в формате JSON
} 