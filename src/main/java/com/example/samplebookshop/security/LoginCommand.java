package com.example.samplebookshop.security;

import lombok.Data;

//an object for mapping json to a java class
@Data
public class LoginCommand {
    private String username;
    private String password;
}
