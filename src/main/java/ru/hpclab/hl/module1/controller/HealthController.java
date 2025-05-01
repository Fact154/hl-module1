package ru.hpclab.hl.module1.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/core")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private final JdbcTemplate jdbcTemplate;
    private final AdminClient kafkaAdminClient;

    public HealthController(JdbcTemplate jdbcTemplate, AdminClient kafkaAdminClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.kafkaAdminClient = kafkaAdminClient;
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

    private boolean checkKafkaConnection() {
        try {
            DescribeClusterResult result = kafkaAdminClient.describeCluster();
            result.nodes().get(3, TimeUnit.SECONDS);
            log.info("Kafka connection is healthy");
            return true;
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
