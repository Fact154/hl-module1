package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.service.VisitorService;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@RestController
@RequestMapping("/visitors")
public class VisitorController {
    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@RequestBody Visitor visitor) {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(visitorService.addVisitor(visitor));
        } finally {
            ObservabilityService.recordTiming("visitor.create", System.currentTimeMillis() - start);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visitor> getVisitor(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            Visitor visitor = visitorService.getVisitor(id);
            return visitor != null ? ResponseEntity.ok(visitor) : ResponseEntity.notFound().build();
        } finally {
            ObservabilityService.recordTiming("visitor.get", System.currentTimeMillis() - start);
        }
    }

    @GetMapping
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        long start = System.currentTimeMillis();
        try {
            return ResponseEntity.ok(visitorService.getAllVisitors());
        } finally {
            ObservabilityService.recordTiming("visitor.getAll", System.currentTimeMillis() - start);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        try {
            visitorService.deleteVisitor(id);
            return ResponseEntity.noContent().build();
        } finally {
            ObservabilityService.recordTiming("visitor.delete", System.currentTimeMillis() - start);
        }
    }
}