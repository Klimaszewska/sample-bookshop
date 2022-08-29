package com.example.samplebookshop.web;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
public class CustomUri {

    private final String path;

    public URI toUri() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path(path).build().toUri();
    }
}
