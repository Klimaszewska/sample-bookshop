package com.example.samplebookshop.catalog.application;

import com.example.samplebookshop.catalog.application.port.CatalogInitializerUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.UpdateBookCoverCommand;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.jpa.BaseEntity;
import com.example.samplebookshop.order.application.port.ManageOrderUseCase;
import com.example.samplebookshop.order.application.port.QueryOrderUseCase;
import com.example.samplebookshop.order.domain.Recipient;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CatalogInitializerService implements CatalogInitializerUseCase {

    private final AuthorJpaRepository authorRepository;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final CatalogUseCase catalog;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void initialize() {
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
        ManageOrderUseCase.PlaceOrderCommand placeOrderCommand = ManageOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new ManageOrderUseCase.OrderItemCommand(effectiveJava.getId(), 16))
                .item(new ManageOrderUseCase.OrderItemCommand(puzzlers.getId(), 7))
                .build();

        ManageOrderUseCase.PlaceOrderResponse placeOrderResponse = manageOrder.placeOrder(placeOrderCommand);
        String result = placeOrderResponse.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        log.info(result);

        // list all orders
        queryOrder.findAll()
                .forEach(order -> log.info("Received order with total price: " + order.getFinalPrice() + " Details: " + order)
                );
    }


    private void initializeData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> csvToBean = new CsvToBeanBuilder<CsvBook>(reader)
                    .withType(CsvBook.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            csvToBean.stream().forEach(this::obtainBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV file", e);
        }
    }

    private void obtainBook(CsvBook csvBook) {
        //parsing authors
        Set<Long> authors = Arrays
                .stream(csvBook.authors.split(","))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(this::obtainAuthor)
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
        //adding the book
        CreateBookCommand command = new CreateBookCommand(
                csvBook.title,
                authors,
                csvBook.year,
                csvBook.amount,
                50L
        );
        Book book = catalog.addBook(command);
        //uploading the thumbnail
        catalog.updateBookCover(updateBookCoverCommand(book.getId(), csvBook.thumbnail));
    }

    private UpdateBookCoverCommand updateBookCoverCommand(Long bookId, String thumbnailUrl) {
        ResponseEntity<byte[]> response = restTemplate.exchange(thumbnailUrl, HttpMethod.GET, null, byte[].class);
        String contentType = response.getHeaders().getContentType().toString();
        return new UpdateBookCoverCommand(bookId, response.getBody(), contentType, "cover");
    }

    private Author obtainAuthor(String name) {
        return authorRepository
                .findByNameIgnoreCase(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;
    }

}

