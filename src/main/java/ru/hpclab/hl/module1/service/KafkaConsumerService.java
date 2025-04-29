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
    public void listen(KafkaMessage kafkaMessage) {
        try {
            logger.debug("Received Kafka message: entity={}, operation={}", 
                kafkaMessage.getEntity(), kafkaMessage.getOperation());

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
                    logger.debug("Ignoring message for entity: {}", kafkaMessage.getEntity());
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", kafkaMessage, e);
        }
    }

    private void handleVisitorMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Visitor newVisitor = objectMapper.readValue(payloadJson, Visitor.class);
                visitorService.addVisitor(newVisitor);
                logger.debug("Added visitor: {}", newVisitor);
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                visitorService.deleteVisitor(Long.parseLong(payloadStr));
                logger.debug("Deleted visitor with ID: {}", payloadStr);
                break;
            default:
                logger.debug("Unknown operation for VISITOR: {}", kafkaMessage.getOperation());
        }
    }

    private void handleExhibitMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Exhibit newExhibit = objectMapper.readValue(payloadJson, Exhibit.class);
                exhibitService.addExhibit(newExhibit);
                logger.debug("Added exhibit: {}", newExhibit);
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                exhibitService.deleteExhibit(Long.parseLong(payloadStr));
                logger.debug("Deleted exhibit with ID: {}", payloadStr);
                break;
            default:
                logger.debug("Unknown operation for EXHIBIT: {}", kafkaMessage.getOperation());
        }
    }

    private void handleTourMessage(KafkaMessage kafkaMessage) throws JsonProcessingException {
        switch (kafkaMessage.getOperation().toUpperCase()) {
            case "POST":
                String payloadJson = objectMapper.writeValueAsString(kafkaMessage.getPayload());
                Tour newTour = objectMapper.readValue(payloadJson, Tour.class);
                tourService.addTour(newTour);
                logger.debug("Added tour: {}", newTour);
                break;
            case "DEL":
                String payloadStr = kafkaMessage.getPayload().toString();
                tourService.deleteTour(Long.parseLong(payloadStr));
                logger.debug("Deleted tour with ID: {}", payloadStr);
                break;
            default:
                logger.debug("Unknown operation for TOUR: {}", kafkaMessage.getOperation());
        }
    }
} 