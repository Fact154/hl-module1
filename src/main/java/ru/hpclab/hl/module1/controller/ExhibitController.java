package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.service.ExhibitService;
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
        return ResponseEntity.ok(exhibitService.addExhibit(exhibit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exhibit> getExhibit(@PathVariable Long id) {
        Exhibit exhibit = exhibitService.getExhibit(id);
        return exhibit != null ? ResponseEntity.ok(exhibit) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Exhibit>> getAllExhibits() {
        return ResponseEntity.ok(exhibitService.getAllExhibits());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExhibit(@PathVariable Long id) {
        exhibitService.deleteExhibit(id);
        return ResponseEntity.noContent().build();
    }
}