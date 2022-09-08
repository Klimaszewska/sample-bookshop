package com.example.samplebookshop.order.application;

import com.example.samplebookshop.catalog.db.BookJpaRepository;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.db.OrderJpaRepository;
import com.example.samplebookshop.order.domain.Order;
import com.example.samplebookshop.order.domain.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {

    private final OrderJpaRepository repository;
    private final BookJpaRepository catalogRepository;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RichOrder> findOneById(Long id) {
        return repository.findById(id)
                .map(this::toRichOrder);
    }


    private RichOrder toRichOrder(Order order) {
        List<RichOrderItem> richItems = toRichItems(order.getItems());
        return new RichOrder(
                order.getId(),
                order.getOrderStatus(),
                richItems,
                order.getRecipient(),
                order.getCreatedAt()
        );
    }

    private List<RichOrderItem> toRichItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    Book book = catalogRepository
                            .findById(item.getBookId())
                            .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
                    return new RichOrderItem(book, item.getQuantity());
                })
                .collect(Collectors.toList());
    }

}
