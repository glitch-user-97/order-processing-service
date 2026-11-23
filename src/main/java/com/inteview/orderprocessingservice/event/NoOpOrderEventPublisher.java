package com.inteview.orderprocessingservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpOrderEventPublisher implements OrderEventPublisher{
    @Override
    public void publishOrderCreated(Long orderId){
        log.info("Kafka disabled; Skipping ORDER_CREATED event for order {}",orderId);
    }

}
