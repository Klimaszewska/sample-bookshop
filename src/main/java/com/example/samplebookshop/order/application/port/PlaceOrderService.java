package com.example.samplebookshop.order.application.port;

import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

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
}
