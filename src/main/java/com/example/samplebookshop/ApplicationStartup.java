package com.example.samplebookshop;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.OrderItem;
import com.example.samplebookshop.order.domain.Recipient;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@AllArgsConstructor
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;


    @Override
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
                .item(new OrderItem(effectiveJava.getId(), 16))
                .item(new OrderItem(puzzlers.getId(), 7))
                .build();

        PlaceOrderResponse placeOrderResponse = manageOrder.placeOrder(placeOrderCommand);
        System.out.println("Created order with id: " + placeOrderResponse.getOrderId());

        // list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("Received order with total price: " + order.totalPrice() + " Details: " + order)
                );
    }

    private void initializeData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorRepository.save(joshua);
        authorRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand("Effective Java", Set.of(joshua.getId()), 2005, new BigDecimal("50.00"));
        CreateBookCommand javaPuzzlers = new CreateBookCommand("Java Puzzlers", Set.of(joshua.getId(), neal.getId()), 2018, new BigDecimal("20.00"));
        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);

    }
}
