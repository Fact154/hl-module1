package ru.hpclab.hl.module1.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.hpclab.hl.module1.model.ExhibitRating;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatisticsClient {
    private final RestTemplate restTemplate;
    private final String statisticsServiceUrl;

    public StatisticsClient(RestTemplate restTemplate, 
                          @Value("${statistics.service.url:http://localhost:8081/api/statistics}") String statisticsServiceUrl) {
        this.restTemplate = restTemplate;
        this.statisticsServiceUrl = statisticsServiceUrl;
    }

    public List<Map.Entry<Long, Long>> getExhibitRating(int year, int month) {
        ExhibitRating[] ratings = restTemplate.getForObject(
                statisticsServiceUrl + "/exhibit-rating?year={year}&month={month}",
                ExhibitRating[].class,
                Map.of("year", year, "month", month)
        );

        return Arrays.stream(ratings)
                .map(rating -> Map.entry(rating.getExhibitId(), rating.getVisitCount()))
                .collect(Collectors.toList());
    }
} 