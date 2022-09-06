package com.example.samplebookshop.catalog.application.port;

import com.example.samplebookshop.catalog.domain.Author;

import java.util.List;

public interface AuthorUseCase {
    List<Author> findAll();
}
