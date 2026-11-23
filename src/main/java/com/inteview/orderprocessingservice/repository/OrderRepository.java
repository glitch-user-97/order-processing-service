package com.inteview.orderprocessingservice.repository;

import com.inteview.orderprocessingservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
