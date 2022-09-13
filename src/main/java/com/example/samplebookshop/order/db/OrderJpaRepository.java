package com.example.samplebookshop.order.db;

import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatusAndCreatedAtLessThanEqual(OrderStatus status, LocalDateTime timestamp);
}
