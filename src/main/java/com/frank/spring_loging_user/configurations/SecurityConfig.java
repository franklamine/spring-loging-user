package com.frank.spring_loging_user.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Définit cette classe comme une configuration Spring
@EnableWebSecurity // Active la sécurité Web de Spring (remplace l'ancien WebSecurityConfigurerAdapter)
@RequiredArgsConstructor // Génère automatiquement un constructeur avec les arguments requis (ici : userDetailsService)
public class SecurityConfig {

    // Injecte un service qui fournit les détails de l'utilisateur (utilisé pour l'authentification)
    private final UserDetailsService userDetailsService;

    // Déclare un bean SecurityFilterChain : c'est ici qu'on définit les règles de sécurité HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Désactive la protection CSRF (utile pour les API REST, mais attention en prod)
                .csrf(csrf -> csrf.disable())

                // Définit les règles d'accès :
                // - "/login" et "/register" sont accessibles à tous
                // - Toute autre requête nécessite une authentification
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/login", "/register")
                                .permitAll() // accessible sans être connecté
                                .anyRequest().authenticated()) // tout le reste nécessite d'être connecté
                .build(); // retourne la chaîne de filtres de sécurité
    }

    // Déclare un bean pour encoder les mots de passe avec BCrypt (algorithme sécurisé)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Déclare un bean AuthenticationManager qui gère l'authentification des utilisateurs
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        // Récupère un builder d'AuthenticationManager depuis HttpSecurity
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Configure le builder avec le service de chargement d'utilisateurs et l'encodeur de mot de passe
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        // Construit et retourne le manager
        return authenticationManagerBuilder.build();
    }
}

