package ru.hpclab.hl.statistics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatisticsCache {
    private final Map<String, List<ExhibitRating>> exhibitRatingCache = new ConcurrentHashMap<>();
    private final Map<String, List<TourDTO>> toursCache = new ConcurrentHashMap<>();

    @Value("${statistics.cache.info:Cache Statistics}")
    private String infoString;

    private final int delay;

    public StatisticsCache(@Value("${statistics.cache.delay:1000}") int delay) {
        this.delay = delay;
    }

    public void putExhibitRating(String key, List<ExhibitRating> ratings) {
        exhibitRatingCache.put(key, ratings);
    }

    public List<ExhibitRating> getExhibitRating(String key) {
        return exhibitRatingCache.get(key);
    }

    public boolean hasExhibitRating(String key) {
        return exhibitRatingCache.containsKey(key);
    }

    public void putTours(String key, List<TourDTO> tours) {
        toursCache.put(key, tours);
    }

    public List<TourDTO> getTours(String key) {
        return toursCache.get(key);
    }

    public boolean hasTours(String key) {
        return toursCache.containsKey(key);
    }

    @Async
    @Scheduled(fixedRateString = "${statistics.cache.print.rate:300000}")
    public void printCacheStatistics() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " - " + infoString + " - " + 
                "Exhibit Rating Cache Size: " + exhibitRatingCache.size() + " - " +
                "Tours Cache Size: " + toursCache.size() + " - " +
                "Total Cache Entries: " + (exhibitRatingCache.size() + toursCache.size()));
        Thread.sleep(delay);
    }
} 