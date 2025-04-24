package ru.hpclab.hl.statistics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hpclab.hl.statistics.service.ObservabilityService;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @GetMapping
    public ObservabilityService.MetricsSnapshot getMetrics() {
        logger.info("Request received for statistics metrics");
        ObservabilityService.MetricsSnapshot snapshot = ObservabilityService.getMetricsAndClean();
        logger.info("Retrieved statistics metrics snapshot: {}", snapshot);
        return snapshot;
    }
}
