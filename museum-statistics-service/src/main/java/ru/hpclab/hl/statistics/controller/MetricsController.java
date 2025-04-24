package ru.hpclab.hl.statistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hpclab.hl.statistics.service.ObservabilityService;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @GetMapping
    public ObservabilityService.MetricsSnapshot getMetrics() {
        return ObservabilityService.getMetricsAndClean();
    }
} 