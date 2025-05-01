package ru.hpclab.hl.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StatisticsCache {
    private final RedisTemplate<String, List<ExhibitRating>> exhibitRatingCache;
    private final RedisTemplate<String, List<TourDTO>> toursCache;

    @Autowired
    public StatisticsCache(RedisTemplate<String, List<ExhibitRating>> exhibitRatingCache,
                           RedisTemplate<String, List<TourDTO>> toursCache) {
        this.exhibitRatingCache = exhibitRatingCache;
        this.toursCache = toursCache;
    }

    @Value("${statistics.cache.info:Cache Statistics}")
    private String infoString;

    public void putExhibitRating(String key, List<ExhibitRating> ratings) {
        long start = System.currentTimeMillis();
        try {
            exhibitRatingCache.opsForValue().set(key, ratings);
        } finally {
            ObservabilityService.recordTiming("cache.putExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public List<ExhibitRating> getExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return exhibitRatingCache.opsForValue().get(key);
        } finally {
            ObservabilityService.recordTiming("cache.getExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public boolean hasExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return exhibitRatingCache.hasKey(key);
        } finally {
            ObservabilityService.recordTiming("cache.hasExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public void putTours(String key, List<TourDTO> tours) {
        long start = System.currentTimeMillis();
        try {
            toursCache.opsForValue().set(key, tours);
        } finally {
            ObservabilityService.recordTiming("cache.putTours", System.currentTimeMillis() - start);
        }
    }

    public List<TourDTO> getTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return toursCache.opsForValue().get(key);
        } finally {
            ObservabilityService.recordTiming("cache.getTours", System.currentTimeMillis() - start);
        }
    }

    public boolean hasTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return toursCache.hasKey(key);
        } finally {
            ObservabilityService.recordTiming("cache.hasTours", System.currentTimeMillis() - start);
        }
    }

    @Async
    // @Scheduled(fixedRateString = "${statistics.cache.print.rate:300000}")
    public void printCacheStatistics() {
        long start = System.currentTimeMillis();
        try {
            log.info("{} - Time: {} - Total Cache Entries: {}", 
                infoString,
                java.time.LocalDateTime.now(),
                exhibitRatingCache.size() + toursCache.size());
        } finally {
            ObservabilityService.recordTiming("cache.printStatistics", System.currentTimeMillis() - start);
        }
    }
} 