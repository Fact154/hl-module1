package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;

@Entity
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitorId;

    private String fullName;
    private int age;
    private String ticketType;

    // Геттеры и сеттеры
}