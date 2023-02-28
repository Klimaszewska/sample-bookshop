package com.example.samplebookshop.web;

import com.example.samplebookshop.catalog.application.port.CatalogInitializerUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Secured({"ROLE_ADMIN"})
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final CatalogInitializerUseCase initializer;


    //security: access for admins only
    @PostMapping("/initialization")
    public void run(String... args) {
        initializer.initialize();
    }


}
