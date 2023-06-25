package com.example.samplebookshop.security;

import com.example.samplebookshop.user.db.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor
public class BookshopUserDetailsService implements UserDetailsService {

    private final UserEntityRepository repository;

    private final AdminConfig adminConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (adminConfig.getUsername().equalsIgnoreCase(username)) {
            return adminConfig.adminUser();
        }
        return repository.findByUsernameIgnoreCase(username)
                .map(UserEntityDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
