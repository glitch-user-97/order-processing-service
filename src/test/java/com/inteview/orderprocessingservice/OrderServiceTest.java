package com.inteview.orderprocessingservice;

import com.inteview.orderprocessingservice.event.OrderEventPublisher;
import com.inteview.orderprocessingservice.exception.OrderNotFoundException;
import com.inteview.orderprocessingservice.model.Order;
import com.inteview.orderprocessingservice.model.OrderStatus;
import com.inteview.orderprocessingservice.model.request.CreateOrderRequest;
import com.inteview.orderprocessingservice.repository.OrderRepository;
import com.inteview.orderprocessingservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository repository;
    @Mock OrderEventPublisher publisher;
    @InjectMocks OrderService service;

    private final CreateOrderRequest request = new CreateOrderRequest("12345","Laptop",1,new BigDecimal("1200"));

    @Test void createOrder_savesWithCreatedStatusAndPublishesEvent() {
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.<Order>getArgument(0));
        Order created = service.createOrder(request);

        assertEquals("12345", created.getCustomerId());
        assertEquals("Laptop", created.getProduct());
        assertEquals(OrderStatus.CREATED, created.getStatus());
        assertNotNull(created.getCreatedAt());
        verify(publisher).publishOrderCreated(any());
    }

    @Test void getOrder_returnsOrder() {
        Order order = new Order("1","Mouse",2,BigDecimal.TEN);
        when(repository.findById(5L)).thenReturn(Optional.of(order));

        assertEquals(order,service.getOrder(5L));
    }

    @Test void getOrder_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,()->service.getOrder(99L));
    }

    @Test void getAllOrders_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(new Order("1","A",1,BigDecimal.ONE),
                new Order("2","B",1,BigDecimal.ONE)));

        assertEquals(2,service.getAllOrders().size());
    }

    @Test void updateStatus_changesStatus() {
        Order order = new Order("1","Mouse",2,BigDecimal.TEN);
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        Order result = service.updateStatus(1L,OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, result.getStatus());
        assertNotNull(result.getUpdatedAt());
    }

    @Test void updateStatus_notFound_ThrowsAndDoesNotSave() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,()->service.updateStatus(7L,OrderStatus.SHIPPED));
        verify(repository, never()).save(any());
    }
}
