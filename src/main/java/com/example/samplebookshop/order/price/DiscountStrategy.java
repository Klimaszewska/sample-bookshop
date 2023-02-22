package com.example.samplebookshop.order.price;

import com.example.samplebookshop.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
