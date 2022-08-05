package com.example.samplebookshop.order.application.port;

import com.example.samplebookshop.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
