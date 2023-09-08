package com.example.nullauthority.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService users() {
        // The builder will ensure the passwords are encoded before saving in memory
        UserDetails superUser = User
                .withUsername("super")
                .password("{noop}password")
                .roles("SUPER").authorities("read", "write", "update", "delete")
                .build();
        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}password")
                .roles("ADMIN").authorities("read", "write", "update")
                .build();
        UserDetails user = User
                .withUsername("user")
                .password("{noop}password")
                .roles("USER").authorities("read", "write")
                .build();
        return new InMemoryUserDetailsManager(user, admin, superUser);
    }

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.PATCH,"/json").permitAll()
                        .requestMatchers("/api/public/super")
                        .access(new WebExpressionAuthorizationManager("hasAuthority('read') && hasAuthority('write') && hasAuthority('update') && hasAuthority('delete')"))
                        .requestMatchers("/api/public/admin")
                        .access(new WebExpressionAuthorizationManager("hasAuthority('read') && hasAuthority('write') && hasAuthority('update')"))
                        .requestMatchers("/api/public/user")
                        .access(new WebExpressionAuthorizationManager("hasAuthority('read') && hasAuthority('write')"))
                        .requestMatchers("/api/public/test").access((authentication, object) -> authorizationDecision(authentication.get(), List.of("read")))
                        .requestMatchers("/api/public/test2").access((authentication, object) -> authorizationDecision(authentication.get(), List.of("read", "delete")))
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    private AuthorizationDecision authorizationDecision(Authentication auth, List<String> filters) {
        List<String> roleUsers = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(filters::contains)
                .toList();
        if (roleUsers.size() == filters.size()) {
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(false);
    }
}
