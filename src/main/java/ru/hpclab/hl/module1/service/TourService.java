package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.repository.TourRepository;
import java.util.List;

@Service
public class TourService {
    private final TourRepository repository;

    public TourService(TourRepository repository) {
        this.repository = repository;
    }

    public Tour addTour(Tour tour) {
        return repository.save(tour);
    }

    public Tour getTour(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Tour> getAllTours() {
        return repository.findAll();
    }

    public void deleteTour(Long id) {
        repository.deleteById(id);
    }
}
