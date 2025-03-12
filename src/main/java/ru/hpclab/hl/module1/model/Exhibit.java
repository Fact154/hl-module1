package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;

@Entity
public class Exhibit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String era;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEra() { return era; }
    public void setEra(String era) { this.era = era; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}