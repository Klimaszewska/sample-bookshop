package com.example.samplebookshop.web;

import com.example.samplebookshop.catalog.application.port.CatalogInitializerUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final CatalogInitializerUseCase initializer;


    @PostMapping("/initialization")
    public void run(String... args) {
        initializer.initialize();
    }


}
