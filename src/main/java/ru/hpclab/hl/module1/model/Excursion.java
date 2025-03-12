package ru.hpclab.hl.module1.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Excursion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long excursionId;

    @ManyToOne
    @JoinColumn(name = "exhibit_id")
    private Exhibit exhibit;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    private Date date;
    private String guide;

    // Геттеры и сеттеры
    public Long getExcursionId() {
        return excursionId;
    }

    public void setExcursionId(Long excursionId) {
        this.excursionId = excursionId;
    }

    public Exhibit getExhibit() {
        return exhibit;
    }

    public void setExhibit(Exhibit exhibit) {
        this.exhibit = exhibit;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }
}
