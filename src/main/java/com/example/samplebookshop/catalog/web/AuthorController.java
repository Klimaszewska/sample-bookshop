package com.example.samplebookshop.catalog.web;

import com.example.samplebookshop.catalog.application.port.AuthorUseCase;
import com.example.samplebookshop.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/authors")
@RestController
@AllArgsConstructor
public class AuthorController {

    private final AuthorUseCase authors;

    //security: access for all users
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Author> findAll() {
        return authors.findAll();
    }

}
