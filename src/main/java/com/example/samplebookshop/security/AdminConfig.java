package com.example.samplebookshop.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Data
@ConfigurationProperties("app.security.admin")
class AdminConfig {
    private String username;
    private String password;
    private Set<String> roles;

    User adminUser() {
        return new User(username,
                password,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
    }
}
