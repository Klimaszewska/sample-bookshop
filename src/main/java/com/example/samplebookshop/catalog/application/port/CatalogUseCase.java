package com.example.samplebookshop.catalog.application.port;

import com.example.samplebookshop.catalog.domain.Book;
import lombok.Value;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findAll();

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    void addBook(CreateBookCommand command);

    void removeById(Long Id);

    void updateBook();

    @Value
    class CreateBookCommand {
        String title;
        String author;
        Integer year;
    }
}
