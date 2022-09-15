package com.example.samplebookshop.catalog.web;

import com.example.samplebookshop.catalog.application.port.CatalogUseCase;
import com.example.samplebookshop.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
class CatalogControllerTest {

    @Autowired
    CatalogController controller;

    @MockBean
    CatalogUseCase catalogUseCase;

    @Test
    void findAll() {
        Book sampleBookOne = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L);
        Book sampleBookTwo = new Book("Java Concurrency", 2006, new BigDecimal("99.00"), 50L);

        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(sampleBookOne, sampleBookTwo));

        List<Book> all = controller.findAll(Optional.empty(), Optional.empty());

        assertEquals(2, all.size());
    }

}
