package com.example.samplebookshop.order.application;

import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.db.OrderJpaRepository;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository orderRepository;
    private final ManageOrderUseCase manageOrderUseCase;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void run(){
        List<Order> ordersTobeAbandoned = orderRepository.findByOrderStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, LocalDateTime.now().minusDays(5));
        ordersTobeAbandoned.forEach(order -> {
            manageOrderUseCase.updateOrderStatus(order.getId(), OrderStatus.ABANDONED);
        });
    }
}
