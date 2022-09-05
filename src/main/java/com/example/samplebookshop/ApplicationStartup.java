package com.example.samplebookshop;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderCommand;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase.PlaceOrderResponse;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.OrderItem;
import com.example.samplebookshop.order.domain.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;

    @Value("${bookshop.catalog.query.title}")
    String title;
    @Value("${bookshop.catalog.query.author}")
    String author;
    @Value("${bookshop.catalog.limit}")
    Long limit;

    public ApplicationStartup(CatalogUseCase catalog, ManageOrderUseCase manageOrder, QueryOrderUseCase queryOrder) {
        this.catalog = catalog;
        this.manageOrder = manageOrder;
        this.queryOrder = queryOrder;
    }

    @Override
    public void run(String... args) {
        initializeData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
        // find Pan Tadeusz
        Book book1 = catalog.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("Cannot find a given book"));
        // find Chlopi
        Book book2 = catalog.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("Cannot find a given book"));

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
                .item(new OrderItem(book1.getId(), 16))
                .item(new OrderItem(book2.getId(), 7))
                .build();

        PlaceOrderResponse placeOrderResponse = manageOrder.placeOrder(placeOrderCommand);
        System.out.println("Created order with id: " + placeOrderResponse.getOrderId());

        // list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("Received order with total price: " + order.totalPrice() + " Details: " + order)
                );
    }

    private void searchCatalog() {
        findByTitle();
        findByAuthor();
        findAndUpdate();
        findByTitle();
        findByAuthor();
    }

    private void findAndUpdate() {
        System.out.println("Updating...");
        catalog.findOneByTitleAndAuthor(title, author).
                ifPresent(book -> {
                    UpdateBookCommand command = UpdateBookCommand.builder()
                            .id(book.getId())
                            .title("Pan Tadeusz - Updated")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Book update result: " + response.isSuccess());
                });
    }

    private void initializeData() {
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("19.90")));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1903, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1900, new BigDecimal("11.90")));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1899, new BigDecimal("14.90")));
    }

    private void findByAuthor() {
        List<Book> booksByAuthor = catalog.findByAuthor(author);
        booksByAuthor.stream().limit(limit).forEach(System.out::println);
    }

    private void findByTitle() {
        List<Book> booksByTitle = catalog.findByTitle(title);
        booksByTitle.stream().limit(limit).forEach(System.out::println);
    }
}
