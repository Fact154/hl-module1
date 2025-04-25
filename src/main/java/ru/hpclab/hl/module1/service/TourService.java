package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.repository.TourRepository;
import ru.hpclab.hl.module1.service.ObservabilityService;
import java.util.List;

@Service
public class TourService {
    private final TourRepository repository;

    public TourService(TourRepository repository) {
        this.repository = repository;
    }

    public Tour addTour(Tour tour) {
        long start = System.currentTimeMillis();
        try {
            return repository.save(tour);
        } finally {
            ObservabilityService.recordTiming("tour.addTour", System.currentTimeMillis() - start);
        }
    }

    public Tour getTour(Long id) {
        long start = System.currentTimeMillis();
        try {
            return repository.findById(id).orElse(null);
        } finally {
            ObservabilityService.recordTiming("tour.getTour", System.currentTimeMillis() - start);
        }
    }

    public List<Tour> getAllTours() {
        long start = System.currentTimeMillis();
        try {
            return repository.findAll();
        } finally {
            ObservabilityService.recordTiming("tour.getAllTours", System.currentTimeMillis() - start);
        }
    }

    public void deleteTour(Long id) {
        long start = System.currentTimeMillis();
        try {
            repository.deleteById(id);
        } finally {
            ObservabilityService.recordTiming("tour.deleteTour", System.currentTimeMillis() - start);
        }
    }
}
