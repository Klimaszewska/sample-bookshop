package com.example.samplebookshop.catalog.application;

import com.example.samplebookshop.catalog.application.port.AuthorUseCase;
import com.example.samplebookshop.catalog.db.AuthorJpaRepository;
import com.example.samplebookshop.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService implements AuthorUseCase {

    private final AuthorJpaRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
}
