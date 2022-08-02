package com.example.samplebookshop;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.domain.Book;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;

    public ApplicationStartup(CatalogUseCase catalog) {
        this.catalog = catalog;
    }

    @Override
    public void run(String... args) {
        List<Book> books = catalog.findByTitle("Pan");
        books.forEach(System.out::println);

        List<Book> booksByAuthor = catalog.findByAuthor("Adam");
        booksByAuthor.forEach(System.out::println);
    }
}
