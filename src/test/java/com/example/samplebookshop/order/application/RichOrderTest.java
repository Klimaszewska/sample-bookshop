package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.domain.OrderItem;
import com.example.samplebookshop.order.domain.OrderStatus;
import com.example.samplebookshop.order.domain.Recipient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RichOrderTest {

    @Test
    void calculateTotalPriceForEmptyOrder() {
        //given
        RichOrder order = new RichOrder(1L,
                OrderStatus.NEW,
                Collections.emptySet(),
                Recipient.builder().build(),
                LocalDateTime.now());

        //when
        BigDecimal totalPrice = order.totalPrice();

        //then
        assertEquals(BigDecimal.ZERO, totalPrice);
    }

    @Test
    void calculateTotalPriceForOrderWithMultipleOrderItems() {
        //given
        Book sampleBook = new Book("Sample Title", 2000, BigDecimal.valueOf(20L), 10L);
        Book sampleBook2 = new Book("Sample Title 2", 2010, BigDecimal.valueOf(30L), 20L);


        OrderItem orderItem = new OrderItem(sampleBook, 1);
        OrderItem orderItem2 = new OrderItem(sampleBook2, 1);
        Set<OrderItem> orderItems = Set.of(orderItem, orderItem2);

        RichOrder order = new RichOrder(1L,
                OrderStatus.NEW,
                orderItems,
                Recipient.builder().build(),
                LocalDateTime.now());

        //when
        BigDecimal totalPrice = order.totalPrice();

        //then
        assertEquals(BigDecimal.valueOf(50L), totalPrice);
    }
}
