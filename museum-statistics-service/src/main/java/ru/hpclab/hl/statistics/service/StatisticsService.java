package ru.hpclab.hl.statistics.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.statistics.client.MuseumClient;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;
import ru.hpclab.hl.statistics.model.ExhibitDTO;
import java.time.LocalDate;
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
        // 1. Получаем все туры и экспонаты
        List<TourDTO> allTours = museumClient.getAllTours();
        Map<Long, String> exhibitNames = museumClient.getAllExhibits().stream()
                .collect(Collectors.toMap(
                        ExhibitDTO::getId,
                        ExhibitDTO::getName
                ));

        // 2. Определяем период для фильтрации
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 3. Фильтруем туры только за указанный месяц
        List<TourDTO> toursInMonth = allTours.stream()
                .filter(tour -> tour.getDate().getYear() == year &&
                              tour.getDate().getMonthValue() == month)
                .collect(Collectors.toList());

        // 4. Подсчитываем количество посещений для каждого экспоната
        Map<Long, Long> visitCounts = toursInMonth.stream()
                .collect(Collectors.groupingBy(
                        TourDTO::getExhibitId,
                        Collectors.counting()
                ));

        // 5. Преобразуем в список ExhibitRating и сортируем по убыванию посещений
        return visitCounts.entrySet().stream()
                .map(entry -> new ExhibitRating(
                        entry.getKey(),                    // ID экспоната
                        exhibitNames.get(entry.getKey()),  // Название экспоната
                        entry.getValue()                   // Количество посещений
                ))
                .sorted((a, b) -> b.getVisitCount().compareTo(a.getVisitCount()))
                .collect(Collectors.toList());
    }
} 