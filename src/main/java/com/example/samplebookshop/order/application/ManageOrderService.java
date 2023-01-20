package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.db.OrderJpaRepository;
import com.example.samplebookshop.order.db.RecipientJpaRepository;
import com.example.samplebookshop.order.domain.*;
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
    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command.getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());

        Order order = Order
                .builder()
                .recipient(obtainRecipient(command.getRecipient()))
                .items(items)
                .build();
        Order savedOrder = orderJpaRepository.save(order);
        Set<Book> updatedBooks = decreaseBookQuantity(items);
        bookJpaRepository.saveAll(updatedBooks);
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Recipient obtainRecipient(Recipient commandRecipient) {
        return recipientJpaRepository
                .findByEmailIgnoreCase(commandRecipient.getEmail())
                .orElse(commandRecipient);
    }

    private Set<Book> decreaseBookQuantity(Set<OrderItem> items) {
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
        checkIfQuantityIsPositive(quantity);
        checkIfQuantityDoesNotExceedAvailableBooks(book, quantity, availableBooks);
        return new OrderItem(book, quantity);
    }

    private void checkIfQuantityDoesNotExceedAvailableBooks(Book book, int quantity, Long availableBooks) {
        if (availableBooks < quantity){
            throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: "
                    + quantity + " of " + availableBooks + " available");
        }
    }

    private void checkIfQuantityIsPositive(int quantity) {
        if (quantity <= 0){
            throw new IllegalArgumentException("Quantity must be more than 0");
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
                    UpdateStatusResult updateStatusResult = order.updateStatus(orderStatus);
                    if (updateStatusResult.isRevoked()) {
                        bookJpaRepository.saveAll(increaseBookQuantity(order.getItems()));
                    }
                    orderJpaRepository.save(order);
                });
    }

    private Set<Book> increaseBookQuantity(Set<OrderItem> items) {
        return items.stream().map(item -> {
            Book book = item.getBook();
            Long availableBooks = book.getAvailableBooks();
            book.setAvailableBooks(availableBooks + item.getQuantity());
            return book;
        }).collect(Collectors.toSet());
    }
}
