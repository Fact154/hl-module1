package ru.hpclab.hl.statistics.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.statistics.client.MuseumClient;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final MuseumClient museumClient;

    public StatisticsService(MuseumClient museumClient) {
        this.museumClient = museumClient;
    }

    public List<ExhibitRating> getExhibitRating(int year, int month) {
        // Получаем все туры
        List<TourDTO> tours = museumClient.getAllTours();

        // Фильтруем туры по месяцу и году
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Группируем туры по ID экспоната и подсчитываем количество посещений
        return tours.stream()
                .filter(tour -> !tour.getDate().isBefore(startDate) && !tour.getDate().isAfter(endDate))
                .collect(Collectors.groupingBy(
                        TourDTO::getExhibitId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                tourList -> new ExhibitRating(
                                        tourList.get(0).getExhibitId(),
                                        tourList.get(0).getExhibitName(),
                                        (long) tourList.size()
                                )
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(ExhibitRating::getVisitCount).reversed())
                .collect(Collectors.toList());
    }
} 