package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.db.OrderJpaRepository;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderItem;
import com.example.samplebookshop.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageOrderService implements ManageOrderUseCase {

    private final OrderJpaRepository orderJpaRepository;
    private final BookJpaRepository bookJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command.getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());

        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(items)
                .build();
        Order savedOrder = orderJpaRepository.save(order);
        Set<Book> updatedBooks = updateBooks(items);
        bookJpaRepository.saveAll(updatedBooks);
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Set<Book> updateBooks(Set<OrderItem> items) {
        return items.stream().map(item -> {
            Book book = item.getBook();
            Long availableBooks = book.getAvailableBooks();
            book.setAvailableBooks(availableBooks - item.getQuantity());
            return book;
        }).collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand orderItemCommand) {
        Book book = bookJpaRepository.getById(orderItemCommand.getBookId());
        int quantity = orderItemCommand.getQuantity();
        Long availableBooks = book.getAvailableBooks();
        if (availableBooks >= quantity) {
            return new OrderItem(book, quantity);
        } else {
            throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: "
                    + quantity + " of " + availableBooks + " available");
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderJpaRepository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus orderStatus) {
        orderJpaRepository.findById(id)
                .ifPresent(order -> {
                    order.updateStatus(orderStatus);
                    orderJpaRepository.save(order);
                });
    }
}
