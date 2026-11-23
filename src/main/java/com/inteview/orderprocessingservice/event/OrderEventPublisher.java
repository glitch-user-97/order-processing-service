package com.inteview.orderprocessingservice.event;

public interface OrderEventPublisher {
    void publishOrderCreated(Long orderId);
}
