package com.example.samplebookshop.order.db;

import com.example.samplebookshop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
