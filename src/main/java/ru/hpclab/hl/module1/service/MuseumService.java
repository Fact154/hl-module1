package ru.hpclab.hl.module1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.model.*;
import ru.hpclab.hl.module1.repository.*;
import java.util.stream.Collectors;
import java.util.*;


@Service
public class MuseumService {
    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ExcursionRepository excursionRepository;

    public void addExhibit(Exhibit exhibit) {
        exhibitRepository.save(exhibit);
    }

    public Visitor addVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public void addExcursion(Excursion excursion) {
        // Убедимся, что Exhibit уже инициализирован в Excursion
        if (excursion.getExhibit() != null) {
            excursion.getExhibit().setVisitCount(excursion.getExhibit().getVisitCount() + 1);
            excursionRepository.save(excursion);
        } else {
            // Логика обработки случая, если exhibit не задан
            System.out.println("Exhibit not found for the excursion.");
        }
    }

    public List<Map.Entry<String, Integer>> getTopExhibits(int month, int year) {
        // Получаем все экскурсии из репозитория
        List<Excursion> excursions = excursionRepository.findAll();

        // Фильтруем экскурсии по месяцу и году
        List<Excursion> filteredExcursions = excursions.stream()
                .filter(excursion -> {
                    // Получаем дату экскурсии
                    Date excursionDate = excursion.getDate();
                    // Проверяем, что месяц и год экскурсии совпадают с переданными параметрами
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(excursionDate);
                    return calendar.get(Calendar.MONTH) == (month - 1) && calendar.get(Calendar.YEAR) == year;
                })
                .collect(Collectors.toList());

        // Создаем карту для подсчета количества посещений для каждого экспоната
        Map<String, Integer> exhibitVisitCount = new HashMap<>();

        for (Excursion excursion : filteredExcursions) {
            Exhibit exhibit = excursion.getExhibit();
            // Инкрементируем счетчик посещений для соответствующего экспоната
            exhibitVisitCount.put(exhibit.getName(), exhibitVisitCount.getOrDefault(exhibit.getName(), 0) + 1);
        }

        // Сортируем по количеству посещений в порядке убывания
        List<Map.Entry<String, Integer>> topExhibits = exhibitVisitCount.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());

        return topExhibits;
    }
}
