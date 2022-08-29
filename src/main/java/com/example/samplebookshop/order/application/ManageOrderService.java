package com.example.samplebookshop.order.application;

import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderRepository;
import com.example.samplebookshop.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageOrderService implements ManageOrderUseCase {

    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order savedOrder = repository.save(order);
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    @Override
    public void deleteOrder(Long id) {
        repository.delete(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus orderStatus) {
        repository.findOneById(id)
                .ifPresent(order -> {
                    order.setOrderStatus(orderStatus);
                    repository.save(order);
                });
    }
}
