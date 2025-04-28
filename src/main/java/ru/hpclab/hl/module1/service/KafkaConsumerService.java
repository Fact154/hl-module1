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
            logger.info("Raw Kafka message: {}", message);
            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
            logger.info("Parsed payload: {}", kafkaMessage.getPayload());

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
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Visitor newVisitor = objectMapper.readValue(payloadJson, Visitor.class);
                visitorService.addVisitor(newVisitor);
                logger.info("Added visitor: {}", newVisitor);
                break;
            case "GET":
                if (kafkaMessage.getPayload() == null) {
                    List<Visitor> visitors = visitorService.getAllVisitors();
                    logger.info("Retrieved all visitors: {}", visitors);
                } else {
                    String payloadStr = kafkaMessage.getPayload().toString();
                    Visitor foundVisitor = visitorService.getVisitor(Long.parseLong(payloadStr));
                    logger.info("Retrieved visitor: {}", foundVisitor);
                }
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                visitorService.deleteVisitor(Long.parseLong(payloadStr));
                logger.info("Deleted visitor with ID: {}", payloadStr);
                break;
            default:
                logger.warn("Unknown operation for VISITOR: {}", kafkaMessage.getOperation());
        }
    }

    private void handleExhibitMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Exhibit newExhibit = objectMapper.readValue(payloadJson, Exhibit.class);
                exhibitService.addExhibit(newExhibit);
                logger.info("Added exhibit: {}", newExhibit);
                break;
            case "GET":
                if (kafkaMessage.getPayload() == null) {
                    List<Exhibit> exhibits = exhibitService.getAllExhibits();
                    logger.info("Retrieved all exhibits: {}", exhibits);
                } else {
                    String payloadStr = kafkaMessage.getPayload().toString();
                    Exhibit foundExhibit = exhibitService.getExhibit(Long.parseLong(payloadStr));
                    logger.info("Retrieved exhibit: {}", foundExhibit);
                }
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                exhibitService.deleteExhibit(Long.parseLong(payloadStr));
                logger.info("Deleted exhibit with ID: {}", payloadStr);
                break;
            default:
                logger.warn("Unknown operation for EXHIBIT: {}", kafkaMessage.getOperation());
        }
    }

    private void handleTourMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Tour newTour = objectMapper.readValue(payloadJson, Tour.class);
                tourService.addTour(newTour);
                logger.info("Added tour: {}", newTour);
                break;
            case "GET":
                if (kafkaMessage.getPayload() == null) {
                    List<Tour> tours = tourService.getAllTours();
                    logger.info("Retrieved all tours: {}", tours);
                } else {
                    String payloadStr = kafkaMessage.getPayload().toString();
                    Tour foundTour = tourService.getTour(Long.parseLong(payloadStr));
                    logger.info("Retrieved tour: {}", foundTour);
                }
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                tourService.deleteTour(Long.parseLong(payloadStr));
                logger.info("Deleted tour with ID: {}", payloadStr);
                break;
            default:
                logger.warn("Unknown operation for TOUR: {}", kafkaMessage.getOperation());
        }
    }
} 