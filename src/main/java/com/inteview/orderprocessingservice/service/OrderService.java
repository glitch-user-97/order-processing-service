package com.inteview.orderprocessingservice.service;

import com.inteview.orderprocessingservice.event.OrderEventPublisher;
import com.inteview.orderprocessingservice.model.Order;
import com.inteview.orderprocessingservice.model.OrderStatus;
import com.inteview.orderprocessingservice.model.request.CreateOrderRequest;
import com.inteview.orderprocessingservice.repository.OrderRepository;
import com.inteview.orderprocessingservice.exception.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepo,OrderEventPublisher orderEventPublisher){
        this.orderRepo = orderRepo;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request){
        Order order = new Order(request.customerId(),request.product(),request.quantity(),request.price());
        Order savedOrder = orderRepo.save(order);
        orderEventPublisher.publishOrderCreated(savedOrder.getId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id){
        return orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(){
        return orderRepo.findAll();
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status){
        Order order = getOrder(id);
        order.changeStatus(status);
        return orderRepo.save(order);
    }

}
