package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.Tour;
import ru.hpclab.hl.module1.repository.TourRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<Map.Entry<Long, Long>> getExhibitRating(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        // Получаем рейтинг выставок через репозиторий с нативным SQL запросом
        List<Object[]> result = repository.findExhibitRatingNative(start, end);

        // Преобразуем результат в список Map.Entry
        return result.stream()
                .map(entry -> Map.entry((Long) entry[0], (Long) entry[1]))
                .collect(Collectors.toList());
    }
}
