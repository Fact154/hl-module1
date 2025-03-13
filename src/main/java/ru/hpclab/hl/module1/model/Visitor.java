package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;

@Entity
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private int age;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    public enum TicketType {
        DISCOUNT, FULL
    }

    public Visitor() {
    }

    public Visitor(String fullName, int age, TicketType ticketType) {
        this.fullName = fullName;
        this.age = age;
        this.ticketType = ticketType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public TicketType getTicketType() { return ticketType; }
    public void setTicketType(TicketType ticketType) { this.ticketType = ticketType; }
}