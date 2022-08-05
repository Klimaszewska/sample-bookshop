package com.example.samplebookshop.order.domain;

import com.example.samplebookshop.catalog.domain.Book;
import lombok.Value;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
