package ru.hpclab.hl.module1.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry; //для повторных запросов
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KillerService {

    private static final Logger log = LoggerFactory.getLogger(KillerService.class);
    private final WebClient webClient;

    public KillerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://main-service-internal:8080").build();
    }

    @Scheduled(fixedRate = 10000)
    @CircuitBreaker(name = "coreServiceCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "coreServiceRetry") //для повторных запросов
    public void killRandomPod() {
        try {
            log.info("Attempting to crash a random pod");
            webClient.get()
                    .uri("/core/crash")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to crash pod", e);
        }
    }

    public void fallback(Exception e) {
        log.error("Fallback: Service unavailable", e);
    }
}
