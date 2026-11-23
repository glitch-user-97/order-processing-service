package com.inteview.orderprocessingservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final String topic;

    public KafkaOrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate, @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishOrderCreated(Long orderId) {
        String payload = "{\"orderId\":\"" + orderId + "\",\"event\":\"ORDER_CREATED\")";
        try {
            kafkaTemplate.send(topic, String.valueOf(orderId), payload);
        }
        catch (Exception e){
            log.error("Failed to publish ORDER_CREATED for order {}", orderId, e);
        }
    }
}
