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
    private final StatisticsCache statisticsCache;

    public StatisticsService(MuseumClient museumClient, StatisticsCache statisticsCache) {
        this.museumClient = museumClient;
        this.statisticsCache = statisticsCache;
    }

    public List<ExhibitRating> getExhibitRating(int year, int month) {
        String cacheKey = String.format("exhibit_rating_%d_%d", year, month);
        
        // Проверяем, есть ли данные в кеше
        if (statisticsCache.hasExhibitRating(cacheKey)) {
            return statisticsCache.getExhibitRating(cacheKey);
        }

        // Получаем все туры
        List<TourDTO> tours = getAllTours();

        // Фильтруем туры по месяцу и году
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Группируем туры по ID экспоната и подсчитываем количество посещений
        List<ExhibitRating> ratings = tours.stream()
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

        // Сохраняем результаты в кеш
        statisticsCache.putExhibitRating(cacheKey, ratings);
        return ratings;
    }

    private List<TourDTO> getAllTours() {
        String cacheKey = "all_tours";
        
        // Проверяем, есть ли туры в кеше
        if (statisticsCache.hasTours(cacheKey)) {
            return statisticsCache.getTours(cacheKey);
        }

        // Получаем туры из клиента и сохраняем в кеш
        List<TourDTO> tours = museumClient.getAllTours();
        statisticsCache.putTours(cacheKey, tours);
        return tours;
    }
} 