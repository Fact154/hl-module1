package ru.hpclab.hl.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
        long start = System.currentTimeMillis();
        try {
            exhibitRatingCache.put(key, ratings);
        } finally {
            ObservabilityService.recordTiming("cache.putExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public List<ExhibitRating> getExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return exhibitRatingCache.get(key);
        } finally {
            ObservabilityService.recordTiming("cache.getExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public boolean hasExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return exhibitRatingCache.containsKey(key);
        } finally {
            ObservabilityService.recordTiming("cache.hasExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public void putTours(String key, List<TourDTO> tours) {
        long start = System.currentTimeMillis();
        try {
            toursCache.put(key, tours);
        } finally {
            ObservabilityService.recordTiming("cache.putTours", System.currentTimeMillis() - start);
        }
    }

    public List<TourDTO> getTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return toursCache.get(key);
        } finally {
            ObservabilityService.recordTiming("cache.getTours", System.currentTimeMillis() - start);
        }
    }

    public boolean hasTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return toursCache.containsKey(key);
        } finally {
            ObservabilityService.recordTiming("cache.hasTours", System.currentTimeMillis() - start);
        }
    }

    @Async
    @Scheduled(fixedRateString = "${statistics.cache.print.rate:300000}")
    public void printCacheStatistics() throws InterruptedException {
        long start = System.currentTimeMillis();
        try {
            log.info("{} - Time: {} - Total Cache Entries: {}", 
                infoString,
                java.time.LocalDateTime.now(),
                exhibitRatingCache.size() + toursCache.size());
            Thread.sleep(delay);
        } finally {
            ObservabilityService.recordTiming("cache.printStatistics", System.currentTimeMillis() - start);
        }
    }
} 