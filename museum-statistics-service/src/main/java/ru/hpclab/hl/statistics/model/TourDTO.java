package ru.hpclab.hl.statistics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class TourDTO {
    @JsonProperty("exhibit")
    private ExhibitInfo exhibit;
    private LocalDate date;

    public static class ExhibitInfo {
        private Long id;
        private String name;
        private String era;
        private String description;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEra() {
            return era;
        }

        public void setEra(String era) {
            this.era = era;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

    public String getExhibitName() {
        return exhibit != null ? exhibit.getName() : null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
} 