package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.service.VisitorService;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/visitors")
public class VisitorController {
    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@RequestBody Visitor visitor) {
        long startNanos = System.nanoTime();
        try {
            return ResponseEntity.ok(visitorService.addVisitor(visitor));
        } finally {
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            ObservabilityService.recordTiming("visitor.create", durationMillis);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visitor> getVisitor(@PathVariable Long id) {
        long startNanos = System.nanoTime();
        try {
            Visitor visitor = visitorService.getVisitor(id);
            return visitor != null ? ResponseEntity.ok(visitor) : ResponseEntity.notFound().build();
        } finally {
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            ObservabilityService.recordTiming("visitor.get", durationMillis);
        }
    }

    @GetMapping
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        long startNanos = System.nanoTime();
        try {
            return ResponseEntity.ok(visitorService.getAllVisitors());
        } finally {
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            ObservabilityService.recordTiming("visitor.getAll", durationMillis);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        long startNanos = System.nanoTime();
        try {
            visitorService.deleteVisitor(id);
            return ResponseEntity.noContent().build();
        } finally {
            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            ObservabilityService.recordTiming("visitor.delete", durationMillis);
        }
    }
}