package ru.hpclab.hl.statistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import ru.hpclab.hl.statistics.model.ExhibitRating;
import ru.hpclab.hl.statistics.model.TourDTO;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsCache {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${statistics.cache.ttl-seconds:1800}")
    private long ttlSeconds;

    private static final String PREFIX_RATING = "exhibit_rating:";
    private static final String PREFIX_TOURS = "tours:";

    public void putExhibitRating(String key, List<ExhibitRating> ratings) {
        long start = System.currentTimeMillis();
        try {
            redisTemplate.opsForValue().set(PREFIX_RATING + key, ratings, Duration.ofSeconds(ttlSeconds));
        } catch (DataAccessException e) {
            log.warn("Redis PUT exhibitRating failed: {}", e.getMessage());
        } finally {
            ObservabilityService.recordTiming("cache.putExhibitRating", System.currentTimeMillis() - start);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ExhibitRating> getExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return (List<ExhibitRating>) redisTemplate.opsForValue().get(PREFIX_RATING + key);
        } catch (DataAccessException e) {
            log.warn("Redis GET exhibitRating failed: {}", e.getMessage());
            return List.of();
        } finally {
            ObservabilityService.recordTiming("cache.getExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public boolean hasExhibitRating(String key) {
        long start = System.currentTimeMillis();
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_RATING + key));
        } finally {
            ObservabilityService.recordTiming("cache.hasExhibitRating", System.currentTimeMillis() - start);
        }
    }

    public void putTours(String key, List<TourDTO> tours) {
        long start = System.currentTimeMillis();
        try {
            redisTemplate.opsForValue().set(PREFIX_TOURS + key, tours, Duration.ofSeconds(ttlSeconds));
        } catch (DataAccessException e) {
            log.warn("Redis PUT tours failed: {}", e.getMessage());
        } finally {
            ObservabilityService.recordTiming("cache.putTours", System.currentTimeMillis() - start);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TourDTO> getTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return (List<TourDTO>) redisTemplate.opsForValue().get(PREFIX_TOURS + key);
        } catch (DataAccessException e) {
            log.warn("Redis GET tours failed: {}", e.getMessage());
            return List.of();
        } finally {
            ObservabilityService.recordTiming("cache.getTours", System.currentTimeMillis() - start);
        }
    }

    public boolean hasTours(String key) {
        long start = System.currentTimeMillis();
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX_TOURS + key));
        } finally {
            ObservabilityService.recordTiming("cache.hasTours", System.currentTimeMillis() - start);
        }
    }

    public void printCacheStatistics() {
        long start = System.currentTimeMillis();
        try {
            Set<String> keys = redisTemplate.keys(PREFIX_RATING + "*");
            Set<String> tourKeys = redisTemplate.keys(PREFIX_TOURS + "*");
            int total = (keys != null ? keys.size() : 0) + (tourKeys != null ? tourKeys.size() : 0);
            log.info("Redis Cache - Total Entries: {}", total);
        } finally {
            ObservabilityService.recordTiming("cache.printStatistics", System.currentTimeMillis() - start);
        }
    }
}
