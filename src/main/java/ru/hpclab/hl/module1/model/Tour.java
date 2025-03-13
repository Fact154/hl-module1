package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Exhibit exhibit;

    @ManyToOne
    private Visitor visitor;

    private LocalDate date;
    private String guideName;

    public Tour() {
    }

    public Tour(Exhibit exhibit, Visitor visitor, LocalDate date, String guideName) {
        this.exhibit = exhibit;
        this.visitor = visitor;
        this.date = date;
        this.guideName = guideName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exhibit getExhibit() { return exhibit; }
    public void setExhibit(Exhibit exhibit) { this.exhibit = exhibit; }
    public Visitor getVisitor() { return visitor; }
    public void setVisitor(Visitor visitor) { this.visitor = visitor; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
}