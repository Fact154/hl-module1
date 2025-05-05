package ru.hpclab.hl.module1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/core")
@ControllerAdvice
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private final KafkaAdmin kafkaAdmin;
    private final JdbcTemplate jdbcTemplate;

    public HealthController(KafkaAdmin kafkaAdmin, JdbcTemplate jdbcTemplate) {
        this.kafkaAdmin = kafkaAdmin;
        this.jdbcTemplate = jdbcTemplate;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error occurred: ", e);
        return ResponseEntity.status(503).body("Service temporarily unavailable");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        boolean isKafkaAlive = checkKafkaConnection();
        boolean isDbAlive = checkDbConnection();

        if (isKafkaAlive && isDbAlive) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(503).body("Unhealthy");
        }
    }

    @PostMapping("/crash")
    public void crash() {
        log.info("Crash endpoint called - throwing exception instead of system exit");
        throw new RuntimeException("Simulated service crash");
    }

    private boolean checkKafkaConnection() {
        try {
            if (kafkaAdmin == null) {
                log.error("KafkaAdmin is not initialized");
                return false;
            }
            
            try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
                adminClient.describeCluster(new DescribeClusterOptions().timeoutMs(3000))
                    .nodes()
                    .get(3, TimeUnit.SECONDS);
                log.info("Kafka connection is healthy");
                return true;
            }
        } catch (Exception e) {
            log.error("Kafka healthcheck failed", e);
            return false;
        }
    }

    private boolean checkDbConnection() {
        try {
            // Простой запрос для проверки подключения к БД
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("Database connection is healthy");
            return true;
        } catch (Exception e) {
            log.error("Database healthcheck failed", e);
            return false;
        }
    }
} 