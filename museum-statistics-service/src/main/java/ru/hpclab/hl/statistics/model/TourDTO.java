package ru.hpclab.hl.statistics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class TourDTO {
    @JsonProperty("exhibit")
    private ExhibitInfo exhibit;
    private LocalDate date;

    public static class ExhibitInfo {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    // Пустой конструктор нужен для десериализации JSON
    public TourDTO() {
    }

    public ExhibitInfo getExhibit() {
        return exhibit;
    }

    public void setExhibit(ExhibitInfo exhibit) {
        this.exhibit = exhibit;
    }

    public Long getExhibitId() {
        return exhibit != null ? exhibit.getId() : null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
} 