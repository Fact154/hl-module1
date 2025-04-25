package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.repository.ExhibitRepository;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@Service
public class ExhibitService {
    private final ExhibitRepository repository;

    public ExhibitService(ExhibitRepository repository) {
        this.repository = repository;
    }

    public Exhibit addExhibit(Exhibit exhibit) {
        long start = System.currentTimeMillis();
        try {
            return repository.save(exhibit);
        } finally {
            ObservabilityService.recordTiming("exhibit.addExhibit", System.currentTimeMillis() - start);
        }
    }

    public Exhibit getExhibit(Long id) {
        long start = System.currentTimeMillis();
        try {
            return repository.findById(id).orElse(null);
        } finally {
            ObservabilityService.recordTiming("exhibit.getExhibit", System.currentTimeMillis() - start);
        }
    }

    public List<Exhibit> getAllExhibits() {
        long start = System.currentTimeMillis();
        try {
            return repository.findAll();
        } finally {
            ObservabilityService.recordTiming("exhibit.getAllExhibits", System.currentTimeMillis() - start);
        }
    }

    public void deleteExhibit(Long id) {
        long start = System.currentTimeMillis();
        try {
            repository.deleteById(id);
        } finally {
            ObservabilityService.recordTiming("exhibit.deleteExhibit", System.currentTimeMillis() - start);
        }
    }
}