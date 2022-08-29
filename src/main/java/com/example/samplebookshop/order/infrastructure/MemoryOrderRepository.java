package com.example.samplebookshop.order.infrastructure;

import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Order> findOneById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public Order save(Order order) {
        if (order.getId() != null){
            storage.put(order.getId(), order);
        } else {
            long nextId = nextId();
            order.setId(nextId);
            order.setCreatedAt(LocalDateTime.now());
            storage.put(nextId, order);
        }
        return order;
    }

    private long nextId() {
        return ID_NEXT_VALUE.incrementAndGet();
    }
}
