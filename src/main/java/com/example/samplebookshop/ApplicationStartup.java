package com.example.samplebookshop;

import com.example.samplebookshop.catalog.application.CatalogController;
import com.example.samplebookshop.catalog.domain.Book;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;

    public ApplicationStartup(CatalogController catalogController) {
        this.catalogController = catalogController;
    }

    @Override
    public void run(String... args) {
        List<Book> books = catalogController.findByTitle("Pan");
        books.forEach(System.out::println);

        List<Book> booksByAuthor = catalogController.findByAuthor("Adam");
        booksByAuthor.forEach(System.out::println);
    }
}
