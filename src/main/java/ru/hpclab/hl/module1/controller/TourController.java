package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.service.TourService;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@RestController
@RequestMapping("/tours")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody Tour tour) {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(tourService.addTour(tour));
        } finally {
            ObservabilityService.recordTiming("tour.create", System.currentTimeMillis() - start);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTour(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            Tour tour = tourService.getTour(id);
            return tour != null ? ResponseEntity.ok(tour) : ResponseEntity.notFound().build();
        } finally {
            ObservabilityService.recordTiming("tour.get", System.currentTimeMillis() - start);
        }
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(tourService.getAllTours());
        } finally {
            ObservabilityService.recordTiming("tour.getAll", System.currentTimeMillis() - start);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            tourService.deleteTour(id);
            return ResponseEntity.noContent().build();
        } finally {
            ObservabilityService.recordTiming("tour.delete", System.currentTimeMillis() - start);
        }
    }
}
