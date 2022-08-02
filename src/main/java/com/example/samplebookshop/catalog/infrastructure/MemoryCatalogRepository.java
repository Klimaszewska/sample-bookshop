package com.example.samplebookshop.catalog.infrastructure;

import com.example.samplebookshop.catalog.domain.Book;
import com.example.samplebookshop.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryCatalogRepository implements CatalogRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public MemoryCatalogRepository() {
        storage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1902));
        storage.put(2L, new Book(1L, "Pan Wołodyjowski", "Henryk Sienkiewicz", 1903));
        storage.put(3L, new Book(2L, "Ogniem i Mieczem", "Henryk Sienkiewicz", 1900));
        storage.put(4L, new Book(3L, "Chłopi", "Władysław Reymont", 1899));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
