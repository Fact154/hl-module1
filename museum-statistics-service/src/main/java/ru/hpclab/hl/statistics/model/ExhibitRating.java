package ru.hpclab.hl.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitRating {
    private Long exhibitId;
    private String exhibitName;
    private Long visitCount;
} 