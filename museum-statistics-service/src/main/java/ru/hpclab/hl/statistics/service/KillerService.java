package ru.hpclab.hl.statistics.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
public class KillerService {

    private static final Logger log = LoggerFactory.getLogger(KillerService.class);
    private final WebClient webClient;
    private final String crashUrl;

    public KillerService(
            WebClient.Builder webClientBuilder,
            @Value("${museum.main-service.url}") String mainServiceUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(mainServiceUrl).build();
        this.crashUrl = "/core/crash";
        log.info("Configured crash endpoint: {}", mainServiceUrl + crashUrl);
    }

    @CircuitBreaker(name = "mainServiceCircuitBreaker", fallbackMethod = "fallback")
    public void killMainServicePod() {
        try {
            log.info("Attempting to crash main service...");
            webClient.post()
                    .uri(crashUrl)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            log.error("Main service DID NOT crash as expected!");
        } catch (Exception e) {
            log.warn("Expected crash behavior: {}", e.getMessage());
        }
    }

    public void fallback(Exception e) {
        log.error("Fallback: Main service unavailable. Error: {}", e.getMessage());
    }
} 