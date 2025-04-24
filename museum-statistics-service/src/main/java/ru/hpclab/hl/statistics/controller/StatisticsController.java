package ru.hpclab.hl.statistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.service.StatisticsService;
import ru.hpclab.hl.statistics.service.ObservabilityService;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/exhibit-rating")
    public ResponseEntity<List<ExhibitRating>> getExhibitRating(
            @RequestParam int year,
            @RequestParam int month) {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(statisticsService.getExhibitRating(year, month));
        } finally {
            ObservabilityService.recordTiming("statistics.exhibit-rating", System.currentTimeMillis() - start);
        }
    }
} 