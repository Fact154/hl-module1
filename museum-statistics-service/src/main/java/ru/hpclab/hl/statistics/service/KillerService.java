package ru.hpclab.hl.statistics.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import java.time.Duration;

@Service
public class KillerService {

    private static final Logger log = LoggerFactory.getLogger(KillerService.class);
    private final WebClient webClient;
    private final String coreServiceUrl;

    public KillerService(@Value("${museum.url}") String coreServiceUrl) {
        this.coreServiceUrl = coreServiceUrl;
        
        ConnectionProvider connectionProvider = ConnectionProvider.builder("killer")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(30))
                        .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(30)));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @CircuitBreaker(name = "coreServiceCircuitBreaker", fallbackMethod = "fallback")
    public void killRandomPod() {
        try {
            log.info("Attempting to crash a pod");
            webClient.post()
                    .uri(coreServiceUrl + "/core/crash")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(e -> {
                        log.warn("Error during crash attempt: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to crash pod: {}", e.getMessage());
            throw e;
        }
    }

    public void fallback(Exception e) {
        log.error("Fallback: Service unavailable. Error: {}", e.getMessage());
    }
} 