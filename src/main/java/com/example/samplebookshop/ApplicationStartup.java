package com.example.samplebookshop;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import com.example.samplebookshop.catalog.domain.Book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    @Value("${bookshop.catalog.query.title}") String title;
    @Value("${bookshop.catalog.query.author}") String author;
    @Value("${bookshop.catalog.limit}") Long limit;

    public ApplicationStartup(CatalogUseCase catalog) {
        this.catalog = catalog;
    }

    @Override
    public void run(String... args) {
        initializeData();
        findByTitle();
        findByAuthor();
    }

    private void initializeData() {
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1902));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1903));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1900));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1899));
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
