package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.service.ExhibitService;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@RestController
@RequestMapping("/exhibits")
public class ExhibitController {
    private final ExhibitService exhibitService;

    public ExhibitController(ExhibitService exhibitService) {
        this.exhibitService = exhibitService;
    }

    @PostMapping
    public ResponseEntity<Exhibit> createExhibit(@RequestBody Exhibit exhibit) {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(exhibitService.addExhibit(exhibit));
        } finally {
            ObservabilityService.recordTiming("exhibit.create", System.currentTimeMillis() - start);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exhibit> getExhibit(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            Exhibit exhibit = exhibitService.getExhibit(id);
            return exhibit != null ? ResponseEntity.ok(exhibit) : ResponseEntity.notFound().build();
        } finally {
            ObservabilityService.recordTiming("exhibit.get", System.currentTimeMillis() - start);
        }
    }

    @GetMapping
    public ResponseEntity<List<Exhibit>> getAllExhibits() {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(exhibitService.getAllExhibits());
        } finally {
            ObservabilityService.recordTiming("exhibit.getAll", System.currentTimeMillis() - start);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibit(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            exhibitService.deleteExhibit(id);
            return ResponseEntity.noContent().build();
        } finally {
            ObservabilityService.recordTiming("exhibit.delete", System.currentTimeMillis() - start);
        }
    }
}