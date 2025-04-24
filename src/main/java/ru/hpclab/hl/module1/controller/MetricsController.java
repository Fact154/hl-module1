package ru.hpclab.hl.module1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hpclab.hl.module1.service.ObservabilityService;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @GetMapping
    public ObservabilityService.MetricsSnapshot getMetrics() {
        logger.info("Request received for metrics");
        ObservabilityService.MetricsSnapshot snapshot = ObservabilityService.getMetricsAndClean();
        logger.info("Retrieved metrics snapshot: {}", snapshot);
        return snapshot;
    }
} 