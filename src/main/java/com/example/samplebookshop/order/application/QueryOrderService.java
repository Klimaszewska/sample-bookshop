package com.example.samplebookshop.order.application;

import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {

    private final OrderRepository repository;

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
}
