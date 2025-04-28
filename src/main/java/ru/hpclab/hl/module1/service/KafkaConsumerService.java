package ru.hpclab.hl.module1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.DTO.KafkaMessage;
import ru.hpclab.hl.module1.model.Visitor;
import ru.hpclab.hl.module1.model.Exhibit;
import ru.hpclab.hl.module1.model.Tour;

import java.util.List;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final VisitorService visitorService;
    private final ExhibitService exhibitService;
    private final TourService tourService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(VisitorService visitorService, 
                              ExhibitService exhibitService,
                              TourService tourService,
                              ObjectMapper objectMapper) {
        this.visitorService = visitorService;
        this.exhibitService = exhibitService;
        this.tourService = tourService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);

            switch (kafkaMessage.getEntity().toUpperCase()) {
                case "VISITOR":
                    handleVisitorMessage(kafkaMessage);
                    break;
                case "EXHIBIT":
                    handleExhibitMessage(kafkaMessage);
                    break;
                case "TOUR":
                    handleTourMessage(kafkaMessage);
                    break;
                default:
                    logger.info("Ignoring message for entity: {}", kafkaMessage.getEntity());
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", message, e);
        }
    }

    private void handleVisitorMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        String payload = kafkaMessage.getPayload();
        logger.info("Raw payload: {}", payload);

        String unescapedPayload = payload.replace("\\\"", "\"");
        Visitor newVisitor = objectMapper.readValue(unescapedPayload, Visitor.class);

        visitorService.addVisitor(newVisitor);
        logger.info("Added visitor: {}", newVisitor);
    }

    private void handleExhibitMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                Exhibit newExhibit = objectMapper.readValue(kafkaMessage.getPayload(), Exhibit.class);
                exhibitService.addExhibit(newExhibit);
                logger.info("Added exhibit: {}", newExhibit);
                break;
            case "GET":
                if (kafkaMessage.getPayload() == null || kafkaMessage.getPayload().isEmpty()) {
                    List<Exhibit> exhibits = exhibitService.getAllExhibits();
                    logger.info("Retrieved all exhibits: {}", exhibits);
                } else {
                    Exhibit foundExhibit = exhibitService.getExhibit(Long.parseLong(kafkaMessage.getPayload()));
                    logger.info("Retrieved exhibit: {}", foundExhibit);
                }
                break;
            case "DEL":
                exhibitService.deleteExhibit(Long.parseLong(kafkaMessage.getPayload()));
                logger.info("Deleted exhibit with ID: {}", kafkaMessage.getPayload());
                break;
            default:
                logger.warn("Unknown operation for EXHIBIT: {}", kafkaMessage.getOperation());
        }
    }

    private void handleTourMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                Tour newTour = objectMapper.readValue(kafkaMessage.getPayload(), Tour.class);
                tourService.addTour(newTour);
                logger.info("Added tour: {}", newTour);
                break;
            case "GET":
                if (kafkaMessage.getPayload() == null || kafkaMessage.getPayload().isEmpty()) {
                    List<Tour> tours = tourService.getAllTours();
                    logger.info("Retrieved all tours: {}", tours);
                } else {
                    Tour foundTour = tourService.getTour(Long.parseLong(kafkaMessage.getPayload()));
                    logger.info("Retrieved tour: {}", foundTour);
                }
                break;
            case "DEL":
                tourService.deleteTour(Long.parseLong(kafkaMessage.getPayload()));
                logger.info("Deleted tour with ID: {}", kafkaMessage.getPayload());
                break;
            default:
                logger.warn("Unknown operation for TOUR: {}", kafkaMessage.getOperation());
        }
    }
} 