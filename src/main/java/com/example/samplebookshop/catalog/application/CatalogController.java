package com.example.samplebookshop.catalog.application;

import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.catalog.domain.CatalogService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public List<Book> findByTitle(String title){
        return catalogService.findByTitle(title);
    }

    public List<Book> findByAuthor(String author){
        return catalogService.findByAuthor(author);
    }
}
