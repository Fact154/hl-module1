package ru.hpclab.hl.statistics.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/killer")
public class KillerController {

    private final WebClient webClient;
    private final String coreServiceUrl;

    public KillerController(@Value("${museum.url}") String coreServiceUrl) {
        this.coreServiceUrl = coreServiceUrl;
        this.webClient = WebClient.builder().build();
    }

    @PostMapping("/kill")
    public void kill() {
        webClient.post()
                .uri(coreServiceUrl + "/core/crash")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
} 