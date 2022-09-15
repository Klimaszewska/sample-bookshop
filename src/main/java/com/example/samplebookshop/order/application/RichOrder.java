package com.example.samplebookshop.order.application;

import com.example.samplebookshop.order.domain.OrderItem;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.order.domain.Recipient;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public
class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;

    public BigDecimal totalPrice() {
        return items.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
