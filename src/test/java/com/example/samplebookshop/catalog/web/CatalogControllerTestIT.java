package com.example.samplebookshop.catalog.web;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import com.example.samplebookshop.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CatalogControllerTestIT {

    @Autowired
    CatalogController controller;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    AuthorJpaRepository authorJpaRepository;

    @Test
    void findAll() {
        givenSampleBookOne();
        givenSampleBookTwo();

        List<Book> books = controller.findAll(Optional.empty(), Optional.empty());

        assertEquals(2, books.size());
    }

    @Test
    void findAllByAuthor() {
        givenSampleBookOne();
        givenSampleBookTwo();

        List<Book> books = controller.findAll(Optional.empty(), Optional.of("Jane"));

        assertEquals(1, books.size());
        assertEquals("Sample Title 2", books.get(0).getTitle());
    }

    private void givenSampleBookTwo() {
        Author author2 = authorJpaRepository.save(new Author("Jane Doe"));
        catalogUseCase.addBook(new CatalogUseCase.CreateBookCommand(
                "Sample Title 2",
                Set.of(author2.getId()),
                2020,
                BigDecimal.valueOf(10L),
                10L
        ));
    }

    private void givenSampleBookOne() {
        Author author1 = authorJpaRepository.save(new Author("John Doe"));
        catalogUseCase.addBook(new CatalogUseCase.CreateBookCommand(
                "Sample Title",
                Set.of(author1.getId()),
                2000,
                BigDecimal.valueOf(50L),
                20L
        ));
    }
}
