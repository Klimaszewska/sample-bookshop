package com.example.samplebookshop.catalog.application.port;

import com.example.samplebookshop.catalog.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {
    List<Book> findAll();

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    Optional<Book> findOneById(Long id);

    Optional<Book> findOneByTitle(String title);

    List<Book> findByTitleAndAuthor(String title, String author);

    Book addBook(CreateBookCommand command);

    void removeById(Long Id);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void updateBookCover(UpdateBookCoverCommand command);

    void removeBookCover(Long id);

    @Value
    class CreateBookCommand {
        String title;
        Set<Long> authorIds;
        Integer year;
        BigDecimal price;
        Long availableBooks;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateBookCommand {
        Long id;
        String title;
        Set<Long> authorIds;
        Integer year;
        BigDecimal price;
    }

    @Value
    class UpdateBookResponse {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }

    @Value
    class UpdateBookCoverCommand {
        Long id;
        byte[] file;
        String contentType;
        String filename;
    }
}
