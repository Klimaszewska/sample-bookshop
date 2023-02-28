package com.example.samplebookshop.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean isOwnerOrAdmin(String owner, User user) {

        return isAdmin(user) || isOwner(owner, user);
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(currentUser -> currentUser.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    private boolean isOwner(String owner, User user) {
        return owner.equalsIgnoreCase(user.getUsername());
    }
}
