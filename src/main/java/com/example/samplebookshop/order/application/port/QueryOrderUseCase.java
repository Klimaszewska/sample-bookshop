package com.example.samplebookshop.order.application.port;

import com.example.samplebookshop.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<Order> findAll();

    Optional<Order> findOneById(Long id);

}
