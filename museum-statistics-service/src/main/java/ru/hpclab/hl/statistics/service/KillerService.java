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
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Service
public class KillerService {

    private static final Logger log = LoggerFactory.getLogger(KillerService.class);
    private final WebClient webClient;
    private final String coreServiceUrl;
    private final Random random = new Random();

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
            // Получаем список всех подов
            List<String> allPods = getPodsList();
            if (allPods.isEmpty()) {
                log.warn("No pods available to check");
                return;
            }

            // Проверяем здоровье каждого пода
            List<String> healthyPods = new ArrayList<>();
            for (String pod : allPods) {
                if (isPodHealthy(pod)) {
                    healthyPods.add(pod);
                }
            }

            if (healthyPods.isEmpty()) {
                log.warn("No healthy pods available to crash");
                return;
            }

            // Выбираем случайный здоровый под
            String randomPod = healthyPods.get(random.nextInt(healthyPods.size()));
            log.info("Selected healthy pod to crash: {}", randomPod);

            // Отправляем запрос на краш выбранному поду
            webClient.post()
                    .uri(coreServiceUrl + "/core/crash")
                    .header("X-Target-Pod", randomPod)
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

    private boolean isPodHealthy(String pod) {
        try {
            String response = webClient.get()
                    .uri(coreServiceUrl + "/core/health")
                    .header("X-Target-Pod", pod)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            boolean isHealthy = "OK".equals(response);
            log.info("Pod {} health check: {}", pod, isHealthy ? "healthy" : "unhealthy");
            return isHealthy;
        } catch (Exception e) {
            log.warn("Failed to check health of pod {}: {}", pod, e.getMessage());
            return false;
        }
    }

    private List<String> getPodsList() {
        // TODO: В реальном приложении здесь должен быть запрос к Kubernetes API
        // Сейчас просто возвращаем список из 3 подов для примера
        return List.of("museum-main-1", "museum-main-2", "museum-main-3");
    }

    public void fallback(Exception e) {
        log.error("Fallback: Service unavailable. Error: {}", e.getMessage());
    }
} 