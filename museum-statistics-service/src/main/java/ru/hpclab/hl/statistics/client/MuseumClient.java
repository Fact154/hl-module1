package ru.hpclab.hl.statistics.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.hpclab.hl.statistics.model.TourDTO;
import ru.hpclab.hl.statistics.model.ExhibitDTO;
import java.util.Arrays;
import java.util.List;

@Component
public class MuseumClient {
    private final RestTemplate restTemplate;
    private final String museumUrl;

    public MuseumClient(RestTemplate restTemplate,
                       @Value("${museum.url}") String museumUrl) {
        this.restTemplate = restTemplate;
        this.museumUrl = museumUrl;
    }

    public List<TourDTO> getAllTours() {
        String url = String.format("%s/tours", museumUrl);
        TourDTO[] tours = restTemplate.getForObject(url, TourDTO[].class);
        return tours != null ? Arrays.asList(tours) : List.of();
    }

    public List<ExhibitDTO> getAllExhibits() {
        String url = String.format("%s/exhibits", museumUrl);
        ExhibitDTO[] exhibits = restTemplate.getForObject(url, ExhibitDTO[].class);
        return exhibits != null ? Arrays.asList(exhibits) : List.of();
    }
} 