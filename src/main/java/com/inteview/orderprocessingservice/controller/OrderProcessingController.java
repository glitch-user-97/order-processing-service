package com.inteview.orderprocessingservice.controller;

import com.inteview.orderprocessingservice.model.request.CreateOrderRequest;
import com.inteview.orderprocessingservice.model.response.OrderResponse;
import com.inteview.orderprocessingservice.model.request.UpdateStatusRequest;
import com.inteview.orderprocessingservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderProcessingController {
    private final OrderService service;

    public OrderProcessingController(OrderService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request){
        return OrderResponse.from(service.createOrder(request));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id){
        return OrderResponse.from(service.getOrder(id));
    }

    @GetMapping
    public java.util.List<OrderResponse> getAll(){
        return service.getAllOrders().stream().map(OrderResponse::from).toList();
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request){
        return OrderResponse.from(service.updateStatus(id, request.status()));
    }
}
