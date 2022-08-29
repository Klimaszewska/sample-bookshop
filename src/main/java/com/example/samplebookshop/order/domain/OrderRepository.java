package com.example.samplebookshop.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    List<Order> findAll();

    Optional<Order> findOneById(Long id);

    void delete(Long id);
}
