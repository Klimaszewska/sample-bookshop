package com.example.samplebookshop.order.application;

import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.db.OrderJpaRepository;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.samplebookshop.order.application.port.ManageOrderUseCase.*;

@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository orderRepository;
    private final ManageOrderUseCase manageOrderUseCase;
    private final OrderProperties properties;

    @Scheduled(cron = "${app.order.abandon-cron}")
    @Transactional
    public void run(){
        Duration paymentPeriod = properties.getPaymentPeriod();
        List<Order> ordersTobeAbandoned = orderRepository.findByOrderStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, LocalDateTime.now().minus(paymentPeriod));
        ordersTobeAbandoned.forEach(order -> {
            //TODO: fix the email reference when implementing security features
            String adminEmail = "admin@example.org";
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, adminEmail);
            manageOrderUseCase.updateOrderStatus(command);
        });
    }
}
