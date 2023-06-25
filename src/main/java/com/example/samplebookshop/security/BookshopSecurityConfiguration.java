package com.example.samplebookshop.security;

import com.example.samplebookshop.user.db.UserEntityRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class BookshopSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserEntityRepository userEntityRepository;
    private final AdminConfig adminConfig;

    @Bean
    User systemUser() {
        return adminConfig.adminUser();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().mvcMatchers(HttpMethod.GET, "/catalog/**", "/upload/**", "/authors/**").permitAll()
                .mvcMatchers(HttpMethod.POST, "/orders", "/login").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @SneakyThrows
    private JsonUsernameAuthenticationFilter getAuthenticationFilter() {
        JsonUsernameAuthenticationFilter filter = new JsonUsernameAuthenticationFilter();
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(createAuthenticationProvider());
    }

    @Bean
    AuthenticationProvider createAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        BookshopUserDetailsService userDetailsService = new BookshopUserDetailsService(userEntityRepository, adminConfig);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(createPasswordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
