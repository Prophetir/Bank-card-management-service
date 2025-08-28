package com.example.bankcards.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RsaKeyAuthorityFilter rsaKeyAuthorityFilter) throws Exception {
        OAuth2AuthorizationServerConfigurer serverConfiguration = authorizationServer();

         http
                 .csrf(AbstractHttpConfigurer::disable)
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/auth/reg", "/auth/log").permitAll()
                         .requestMatchers("/card/admin/**").hasRole("ADMIN")
                         .requestMatchers("/card/user/**").hasRole("USER")
                         .anyRequest().authenticated()
                 )
                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .addFilterAfter(rsaKeyAuthorityFilter, UsernamePasswordAuthenticationFilter.class);
                 //.with(serverConfiguration, Customizer.withDefaults());

         return http.build();
    }
}
