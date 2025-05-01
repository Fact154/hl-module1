package ru.hpclab.hl.module1.service;

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
    public void killRandomPod() {
        try {
            log.info("Attempting to crash a random pod");
            webClient.get()
                    .uri("/api/core/crash")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to crash pod", e);
        }
    }
}
