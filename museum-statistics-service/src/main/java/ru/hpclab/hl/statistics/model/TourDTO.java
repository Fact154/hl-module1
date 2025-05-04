package ru.hpclab.hl.statistics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class TourDTO {
    @JsonProperty("exhibit")
    private ExhibitInfo exhibit;
    private LocalDate date;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExhibitInfo {
        private Long id;
        private String name;
        private String era;
        private String description;
    }

    public Long getExhibitId() {
        return exhibit != null ? exhibit.getId() : null;
    }

    public String getExhibitName() {
        return exhibit != null ? exhibit.getName() : null;
    }
}
