package com.example.samplebookshop.web;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.OrderItemCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.Recipient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogUseCase catalog;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;


    @PostMapping("/data")
    public void run(String... args) {
        initializeData();
        placeOrder();
    }

    private void placeOrder() {
        // find Pan Tadeusz
        Book effectiveJava = catalog.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("Cannot find a given book"));
        // find Chlopi
        Book puzzlers = catalog.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("Cannot find a given book"));
        // create Recipient
        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("503710835")
                .street("Sample Street 1")
                .city("Sample City")
                .zipCode("00-000")
                .email("sample@sample.com")
                .build();

        // place order command
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItemCommand(effectiveJava.getId(), 16))
                .item(new OrderItemCommand(puzzlers.getId(), 7))
                .build();

        PlaceOrderResponse placeOrderResponse = manageOrder.placeOrder(placeOrderCommand);
        log.info("Created order with id: " + placeOrderResponse.getOrderId());

        // list all orders
        queryOrder.findAll()
                .forEach(order -> log.info("Received order with total price: " + order.totalPrice() + " Details: " + order)
                );
    }

    private void initializeData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorRepository.save(joshua);
        authorRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand("Effective Java", Set.of(joshua.getId()), 2005, new BigDecimal("50.00"), 50L);
        CreateBookCommand javaPuzzlers = new CreateBookCommand("Java Puzzlers", Set.of(joshua.getId(), neal.getId()), 2018, new BigDecimal("20.00"), 50L);
        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);

    }
}
