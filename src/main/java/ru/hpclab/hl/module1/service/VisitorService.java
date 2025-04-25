package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.repository.VisitorRepository;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@Service
public class VisitorService {
    private final VisitorRepository repository;

    public VisitorService(VisitorRepository repository) {
        this.repository = repository;
    }

    public Visitor addVisitor(Visitor visitor) {
        long start = System.currentTimeMillis();
        try {
            return repository.save(visitor);
        } finally {
            ObservabilityService.recordTiming("visitor.addVisitor", System.currentTimeMillis() - start);
        }
    }

    public Visitor getVisitor(Long id) {
        long start = System.currentTimeMillis();
        try {
            return repository.findById(id).orElse(null);
        } finally {
            ObservabilityService.recordTiming("visitor.getVisitor", System.currentTimeMillis() - start);
        }
    }

    public List<Visitor> getAllVisitors() {
        long start = System.currentTimeMillis();
        try {
            return repository.findAll();
        } finally {
            ObservabilityService.recordTiming("visitor.getAllVisitors", System.currentTimeMillis() - start);
        }
    }

    public void deleteVisitor(Long id) {
        long start = System.currentTimeMillis();
        try {
            repository.deleteById(id);
        } finally {
            ObservabilityService.recordTiming("visitor.deleteVisitor", System.currentTimeMillis() - start);
        }
    }
}
