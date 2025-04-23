package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.service.TourService;
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
        return ResponseEntity.ok(tourService.addTour(tour));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTour(@PathVariable Long id) {
        Tour tour = tourService.getTour(id);
        return tour != null ? ResponseEntity.ok(tour) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}
