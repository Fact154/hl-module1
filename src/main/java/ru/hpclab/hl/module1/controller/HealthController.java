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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleCrashException(RuntimeException e) {
        if (e.getMessage().equals("Simulated service crash")) {
            log.info("Service crash simulated successfully");
            return ResponseEntity.ok("Service crash initiated");
        }
        return handleException(e);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(@RequestHeader(value = "X-Target-Pod", required = false) String targetPod) {
        // Получаем имя текущего пода из переменной окружения
        String currentPod = System.getenv("HOSTNAME");
        
        // Если это не целевой под, просто возвращаем 503
        if (targetPod != null && !targetPod.equals(currentPod)) {
            log.info("This pod ({}) is not the target ({}) for health check", currentPod, targetPod);
            return ResponseEntity.status(503).body("Not the target pod");
        }

        boolean isKafkaAlive = checkKafkaConnection();
        boolean isDbAlive = checkDbConnection();

        if (isKafkaAlive && isDbAlive) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(503).body("Unhealthy");
        }
    }

    @PostMapping("/crash")
    public ResponseEntity<String> crash() {
        String currentPod = System.getenv("HOSTNAME");
        log.info("Crash endpoint called - initiating pod crash for pod: {}", currentPod);
        
        // Возвращаем 200 OK перед крашем
        ResponseEntity<String> response = ResponseEntity.ok("Service crash initiated");
        // Запускаем краш в отдельном потоке, чтобы успеть отправить ответ
        new Thread(() -> {
            try {
                Thread.sleep(100); // Даем время на отправку ответа
                System.exit(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        return response;
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